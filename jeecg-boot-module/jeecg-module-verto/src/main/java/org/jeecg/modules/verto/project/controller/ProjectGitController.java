package org.jeecg.modules.verto.project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jeecg.modules.verto.oauth.config.GitHubOAuthProperties;
import org.jeecg.modules.verto.oauth.config.GitlabOAuthProperties;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * 项目Git相关接口
 * 前端调用：/jeecgboot/project/git/repos?query=xxx
 * 实际后端路径：/jeecg-boot/project/git/repos
 */
@Tag(name = "项目管理")
@RestController
@RequestMapping("/project/git")
@RequiredArgsConstructor
public class ProjectGitController {
    private static final Logger log = LoggerFactory.getLogger(ProjectGitController.class);

    private final IOAuthService oauthService;
    private final GitlabOAuthProperties gitlabProps;
    private final GitHubOAuthProperties githubProps;

    @GetMapping("/repos")
    public Result<List<Map<String, Object>>> listRepos(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "platform", required = false) String platform
    ) {
        try {
            // Diagnostic: log which IOAuthService implementation is injected
            log.debug("listRepos: oauthService impl = {}", oauthService.getClass().getName());
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (sysUser == null) {
                return Result.error("未获取到登录用户，请先登录后再试");
            }

            // 选择平台：优先使用传入的platform，其次使用已绑定的平台（gitlab优先，其次github）
            String usePlatform = platform;
            if (!StringUtils.hasText(usePlatform)) {
                Map<String, Object> bindings = oauthService.getUserBindings(sysUser.getId());
                if (bindings.containsKey("gitlab")) {
                    usePlatform = "gitlab";
                } else {
                    // 暂不在getUserBindings返回github，默认尝试github
                    usePlatform = "github";
                }
            }

            String accessToken = oauthService.getAccessTokenForSystemUser(sysUser.getId(), usePlatform);
            if (!StringUtils.hasText(accessToken)) {
                // 未绑定则返回空列表，前端可提示绑定账号
                log.warn("user {} has no bound {} account", sysUser.getId(),sysUser.getUsername(), usePlatform);
                return Result.OK("未绑定" + usePlatform + "账号", Collections.emptyList());
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);

            List<Map<String, Object>> repos = new ArrayList<>();

            if ("gitlab".equalsIgnoreCase(usePlatform)) {
                // 仅查询当前用户可访问的项目，按名称模糊匹配
                String server = gitlabProps.getServer();
                String url = server + "/api/v4/projects?membership=true&simple=true&per_page=50";
                if (StringUtils.hasText(query)) {
                    url += "&search=" + query;
                }
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
                List<?> body = resp.getBody();
                if (body != null) {
                    for (Object o : body) {
                        if (o instanceof Map) {
                            Map m = (Map) o;
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", m.get("id"));
                            item.put("name", m.get("name"));
                            item.put("full_name", m.get("path_with_namespace"));
                            item.put("web_url", m.get("web_url"));
                            item.put("default_branch", m.get("default_branch"));
                            item.put("visibility", m.get("visibility"));
                            repos.add(item);
                        }
                    }
                }
            } else {
                // GitHub：列出当前用户的仓库，使用token（如已绑定）
                String url = "https://api.github.com/user/repos?per_page=50&visibility=all";
                HttpHeaders ghHeaders = new HttpHeaders();
                ghHeaders.set("Authorization", "Bearer " + accessToken);
                ghHeaders.set("Accept", "application/vnd.github+json");
                HttpEntity<Void> entity = new HttpEntity<>(ghHeaders);
                ResponseEntity<List> resp = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);
                List<?> body = resp.getBody();
                if (body != null) {
                    for (Object o : body) {
                        if (o instanceof Map) {
                            Map m = (Map) o;
                            String name = Objects.toString(m.get("name"), "");
                            if (StringUtils.hasText(query) && !name.toLowerCase().contains(query.toLowerCase())) {
                                continue;
                            }
                            Map<String, Object> item = new HashMap<>();
                            item.put("id", m.get("id"));
                            item.put("name", name);
                            item.put("full_name", m.get("full_name"));
                            item.put("web_url", m.get("html_url"));
                            item.put("default_branch", m.get("default_branch"));
                            item.put("visibility", ((Boolean) Optional.ofNullable(m.get("private")).orElse(Boolean.TRUE)) ? "private" : "public");
                            repos.add(item);
                        }
                    }
                }
            }

            return Result.OK(repos);
        } catch (Exception e) {
            log.error("list repos error", e);
            return Result.error("获取仓库失败: " + e.getMessage());
        }
    }

    /**
     * 通用Git API代理 (GET)：使用当前登录用户绑定的 access_token 转发到 GitHub/GitLab
     * 用法示例：
     * - GitHub：/project/git/proxy?path=/user/repos
     * - GitLab：/project/git/proxy?path=/api/v4/projects&platform=gitlab
     */
    @GetMapping("/proxy")
    public Result<Object> proxyGitGet(
            @RequestParam("path") String path,
            @RequestParam(value = "platform", required = false) String platform,
            HttpServletRequest request
    ) {
        try {
            // Diagnostic: log which IOAuthService implementation is injected
            log.debug("proxyGitGet: oauthService impl = {}", oauthService.getClass().getName());
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (sysUser == null) {
                return Result.error("未获取到登录用户，请先登录后再试");
            }

            // 选择平台：优先使用传入的platform，其次使用已绑定的平台（gitlab优先，其次github）
            String usePlatform = platform;
            if (!StringUtils.hasText(usePlatform)) {
                Map<String, Object> bindings = oauthService.getUserBindings(sysUser.getId());
                if (bindings.containsKey("gitlab")) {
                    usePlatform = "gitlab";
                } else {
                    usePlatform = "github";
                }
            }

            String accessToken = oauthService.getAccessTokenForSystemUser(sysUser.getId(), usePlatform);
            if (!StringUtils.hasText(accessToken)) {
                return Result.error("未绑定" + usePlatform + "账号或未获取到token");
            }

            // 构建目标URL并做SSRF防护
            String base;
            HttpHeaders headers = new HttpHeaders();
            if ("gitlab".equalsIgnoreCase(usePlatform)) {
                base = gitlabProps.getServer();
                headers.set("Authorization", "Bearer " + accessToken);
            } else {
                base = "https://api.github.com";
                headers.set("Authorization", "Bearer " + accessToken);
                headers.set("Accept", "application/vnd.github+json");
            }

            // 仅允许以 "/" 开头的相对路径，禁止带协议/域名，避免SSRF
            if (!StringUtils.hasText(path) || path.startsWith("http://") || path.startsWith("https://")) {
                return Result.error("非法path参数，仅允许以/开头的相对API路径");
            }
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(base + path);
            // 透传原请求的查询参数
            if (request.getParameterMap() != null) {
                request.getParameterMap().forEach((k, v) -> {
                    if (!"path".equals(k) && !"platform".equals(k) && v != null) {
                        for (String vv : v) {
                            builder.queryParam(k, vv);
                        }
                    }
                });
            }

            RestTemplate rt = new RestTemplate();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> resp = rt.exchange(builder.build(true).toUri(), HttpMethod.GET, entity, Object.class);
            return Result.OK(resp.getBody());
        } catch (Exception e) {
            log.error("proxy git api error", e);
            return Result.error("代理Git接口失败: " + e.getMessage());
        }
    }
}