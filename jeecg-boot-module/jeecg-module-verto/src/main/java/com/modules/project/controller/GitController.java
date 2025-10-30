package com.verto.modules.project.controller;

import com.verto.common.api.Result;
import com.verto.modules.project.dto.GitRepoCreateRequest;
import com.verto.modules.project.entity.Project;
import com.verto.modules.project.service.IProjectService;
import com.verto.modules.appmanage.entity.AppManage;
import com.verto.modules.appmanage.service.IAppManageService;
import com.verto.modules.oauth.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "Git仓库管理", description = "Git仓库创建与权限校验接口")
@RestController
@RequestMapping("/project/git")
@Slf4j
public class GitController {

    @Autowired
    @Qualifier("githubRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IAppManageService appManageService;

    @Autowired
    private OAuthService oauthService;

    // 统一解析本次请求应使用的 GitHub 访问令牌：优先入参 token，其次 Authorization 头，最后根据当前系统用户绑定信息从后端获取
    private String resolveUserToken(jakarta.servlet.http.HttpServletRequest request, String token) {
        if (token != null && !token.trim().isEmpty()) {
            return token.trim();
        }
        if (request != null) {
            String auth = request.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                return auth.substring(7).trim();
            }
            // 从请求头解析系统用户ID（X-User-Id）
            String currentUserId = request.getHeader("X-User-Id");
            if (currentUserId == null || currentUserId.trim().isEmpty()) {
                return null; // 用户ID缺失，无法获取令牌
            }
            try {
                String dbToken = oauthService.getAccessTokenForSystemUser(currentUserId, "github");
                if (dbToken != null && !dbToken.trim().isEmpty()) {
                    return dbToken.trim();
                }
            } catch (Exception e) {
                log.warn("后端令牌解析失败: {}", e.getMessage());
            }
        }
        return null;
    }

    @Operation(summary = "创建Git仓库（当前支持GitHub）")
    @PostMapping("/repo/create")
    public Result<Map<String, Object>> createRepo(@RequestBody GitRepoCreateRequest req,
                                                  jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (req.getGitUrl() == null || req.getGitUrl().trim().isEmpty()) {
                return Result.error("gitUrl不能为空");
            }
            URI uri = URI.create(req.getGitUrl());
            String host = uri.getHost();
            if (host == null) {
                // 可能是无协议的地址，做简单兜底
                String url = req.getGitUrl().replace("https://", "").replace("http://", "");
                int idx = url.indexOf('/');
                host = idx > 0 ? url.substring(0, idx) : url;
            }

            String token = resolveUserToken(request, req.getToken());

            if (host.contains("github.com")) {
                return createGithubRepo(req, token);
            }

            return Result.error("暂不支持该Git提供商: " + host);
        } catch (Exception e) {
            log.error("创建Git仓库异常", e);
            return Result.error("创建Git仓库异常: " + e.getMessage());
        }
    }

    @Operation(summary = "校验Git权限（当前支持GitHub）")
    @GetMapping("/permission/check")
    public Result<Map<String, Object>> checkPermission(@RequestParam("gitUrl") String gitUrl,
                                                       @RequestParam(value = "token", required = false) String token,
                                                       jakarta.servlet.http.HttpServletRequest request) {
        try {
            URI uri = URI.create(gitUrl);
            String host = uri.getHost();
            if (host == null) {
                String url = gitUrl.replace("https://", "").replace("http://", "");
                int idx = url.indexOf('/');
                host = idx > 0 ? url.substring(0, idx) : url;
            }
            if (!host.contains("github.com")) {
                return Result.error("暂不支持该Git提供商: " + host);
            }

            String resolvedToken = resolveUserToken(request, token);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github+json");
            if (resolvedToken != null && !resolvedToken.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + resolvedToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity, Map.class);
            Map<String, Object> data = new HashMap<>();
            data.put("login", resp.getBody() != null ? resp.getBody().get("login") : null);
            data.put("ok", resp.getStatusCode().is2xxSuccessful());
            return Result.ok(data);
        } catch (HttpClientErrorException e) {
            return Result.error("权限校验失败: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("校验Git权限异常", e);
            return Result.error("校验Git权限异常: " + e.getMessage());
        }
    }

    @Operation(summary = "搜索当前用户可访问的Git仓库（当前支持GitHub）")
    @GetMapping("/repos")
    public Result<Map<String, Object>> listRepos(@RequestParam(value = "query", required = false) String query,
                                                @RequestParam(value = "token", required = false) String token,
                                                jakarta.servlet.http.HttpServletRequest request) {
        try {
            String resolvedToken = resolveUserToken(request, token);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github+json");
            if (resolvedToken != null && !resolvedToken.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + resolvedToken);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 获取当前用户可访问的仓库列表（第一页）
            ResponseEntity<Object[]> resp = restTemplate.exchange("https://api.github.com/user/repos", HttpMethod.GET, entity, Object[].class);
            Object[] repos = resp.getBody();
            Map<String, Object> data = new HashMap<>();
            if (repos == null) {
                data.put("repos", new Object[0]);
                return Result.ok(data);
            }
            // 简单过滤（包含关键字）
            java.util.List<Map<String, Object>> list = new java.util.ArrayList<>();
            for (Object o : repos) {
                if (!(o instanceof Map)) continue;
                Map<?, ?> m = (Map<?, ?>) o;
                String name = m.get("name") != null ? m.get("name").toString() : "";
                String cloneUrl = m.get("clone_url") != null ? m.get("clone_url").toString() : "";
                if (query == null || query.trim().isEmpty() || name.toLowerCase().contains(query.toLowerCase())) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", name);
                    item.put("clone_url", cloneUrl);
                    list.add(item);
                }
            }
            data.put("repos", list);
            return Result.ok(data);
        } catch (HttpClientErrorException e) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("repos", new Object[0]);
            return Result.ok("获取仓库列表失败: " + e.getStatusCode(), empty);
        } catch (Exception e) {
            log.error("获取仓库列表异常", e);
            Map<String, Object> empty = new HashMap<>();
            empty.put("repos", new Object[0]);
            return Result.ok("获取仓库列表异常: " + e.getMessage(), empty);
        }
    }

    @Operation(summary = "获取项目的Git分支列表")
    @GetMapping("/branches")
    public Result<java.util.List<java.util.Map<String, Object>>> getBranches(
            @Parameter(description = "项目ID") @RequestParam(name = "projectId", required = true) String projectId,
            @Parameter(description = "Git访问Token，可选；不传则尝试从请求头 Authorization: Bearer 中读取") @RequestParam(name = "token", required = false) String token,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 1) 查询项目
            Project project = projectService.getById(projectId);
            if (project == null) {
                return Result.error("未找到项目: " + projectId);
            }
            String appId = project.getRelatedAppId();
            if (appId == null || appId.trim().isEmpty()) {
                return Result.error("项目未关联应用，无法获取Git仓库地址");
            }

            // 2) 查询应用以获取 gitUrl
            AppManage app = appManageService.getById(appId);
            if (app == null) {
                return Result.error("未找到关联应用: " + appId);
            }
            String gitUrl = app.getGitUrl();
            if (gitUrl == null || gitUrl.trim().isEmpty()) {
                return Result.error("关联应用未配置Git仓库地址");
            }

            // 3) 解析 owner/repo
            String[] ownerRepo = parseOwnerRepo(gitUrl);
            if (ownerRepo == null) {
                return Result.error("无法解析Git仓库地址: " + gitUrl);
            }
            String owner = ownerRepo[0];
            String repo = ownerRepo[1];

            String resolvedToken = resolveUserToken(request, token);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Accept", "application/vnd.github+json");
            if (resolvedToken != null && !resolvedToken.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + resolvedToken);
            }
            org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

            // 4) 调用 GitHub API 获取分支
            String branchesUrl = String.format("https://api.github.com/repos/%s/%s/branches?per_page=100", owner, repo);
            org.springframework.http.ResponseEntity<Object[]> resp = restTemplate.exchange(branchesUrl, org.springframework.http.HttpMethod.GET, entity, Object[].class);
            Object[] branches = resp.getBody();

            java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
            if (branches != null) {
                for (Object o : branches) {
                    if (!(o instanceof java.util.Map)) continue;
                    java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                    String name = m.get("name") != null ? m.get("name").toString() : null;
                    Object protectedObj = m.get("protected");
                    Object commitObj = m.get("commit");
                    String sha = null;
                    if (commitObj instanceof java.util.Map) {
                        Object shaObj = ((java.util.Map<?, ?>) commitObj).get("sha");
                        if (shaObj != null) sha = shaObj.toString();
                    }
                    if (name != null) {
                        java.util.Map<String, Object> item = new java.util.HashMap<>();
                        item.put("name", name);
                        item.put("protected", java.lang.Boolean.TRUE.equals(protectedObj));
                        item.put("sha", sha);
                        // 简单状态：默认标记为 active
                        item.put("status", "active");
                        list.add(item);
                    }
                }
            }
            return Result.ok(list);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.warn("获取分支列表失败: {}", e.getMessage());
            return Result.error("获取分支列表失败: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("获取分支列表异常", e);
            return Result.error("获取分支列表异常: " + e.getMessage());
        }
    }

    @Operation(summary = "获取项目指定分支的提交记录（当前支持GitHub）")
    @GetMapping("/commits")
    public Result<java.util.List<java.util.Map<String, Object>>> getCommits(
            @Parameter(description = "项目ID") @RequestParam(name = "projectId", required = true) String projectId,
            @Parameter(description = "分支名称，不传则尝试使用项目的 gitBranch 字段") @RequestParam(name = "branch", required = false) String branch,
            @Parameter(description = "页码") @RequestParam(name = "page", defaultValue = "1") Integer page,
            @Parameter(description = "每页大小（GitHub最多100）") @RequestParam(name = "pageSize", defaultValue = "50") Integer pageSize,
            @Parameter(description = "Git访问Token，可选；不传则尝试从请求头 Authorization: Bearer 中读取") @RequestParam(name = "token", required = false) String token,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // 1) 查询项目，获取关联应用及 gitUrl
            Project project = projectService.getById(projectId);
            if (project == null) {
                return Result.error("未找到项目: " + projectId);
            }
            String appId = project.getRelatedAppId();
            if (appId == null || appId.trim().isEmpty()) {
                return Result.error("项目未关联应用，无法获取Git仓库地址");
            }

            AppManage app = appManageService.getById(appId);
            if (app == null) {
                return Result.error("未找到关联应用: " + appId);
            }
            String gitUrl = app.getGitUrl();
            if (gitUrl == null || gitUrl.trim().isEmpty()) {
                return Result.error("关联应用未配置Git仓库地址");
            }

            // 默认分支：若未传则尝试使用项目的 gitBranch
            if (branch == null || branch.trim().isEmpty()) {
                branch = project.getGitBranch();
            }
            if (branch == null || branch.trim().isEmpty()) {
                return Result.error("未指定分支，且项目未配置默认分支");
            }

            // 2) 解析 owner/repo
            String[] ownerRepo = parseOwnerRepo(gitUrl);
            if (ownerRepo == null) {
                return Result.error("无法解析Git仓库地址: " + gitUrl);
            }
            String owner = ownerRepo[0];
            String repo = ownerRepo[1];

            // 3) 统一解析令牌（优先入参token，其次Authorization头，最后根据系统用户绑定信息从后端获取）
            String resolvedToken = resolveUserToken(request, token);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Accept", "application/vnd.github+json");
            if (resolvedToken != null && !resolvedToken.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + resolvedToken);
            }
            org.springframework.http.HttpEntity<Void> entity = new org.springframework.http.HttpEntity<>(headers);

            // 4) 调用 GitHub API 获取提交列表
            if (pageSize == null || pageSize <= 0) pageSize = 50;
            if (pageSize > 100) pageSize = 100; // GitHub per_page 上限 100
            if (page == null || page <= 0) page = 1;

            String commitsUrl = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s&per_page=%d&page=%d",
                    owner, repo, java.net.URLEncoder.encode(branch, java.nio.charset.StandardCharsets.UTF_8), pageSize, page);
            org.springframework.http.ResponseEntity<Object[]> resp = restTemplate.exchange(commitsUrl, org.springframework.http.HttpMethod.GET, entity, Object[].class);
            Object[] commits = resp.getBody();

            java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
            if (commits != null) {
                for (Object o : commits) {
                    if (!(o instanceof java.util.Map)) continue;
                    java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                    String sha = m.get("sha") != null ? m.get("sha").toString() : null;
                    String htmlUrl = m.get("html_url") != null ? m.get("html_url").toString() : null;
                    String message = null;
                    String author = null;
                    String date = null;
                    Object commitObj = m.get("commit");
                    if (commitObj instanceof java.util.Map) {
                        Object msgObj = ((java.util.Map<?, ?>) commitObj).get("message");
                        if (msgObj != null) message = msgObj.toString();
                        Object authorObj = ((java.util.Map<?, ?>) commitObj).get("author");
                        if (authorObj instanceof java.util.Map) {
                            Object nameObj = ((java.util.Map<?, ?>) authorObj).get("name");
                            Object dateObj = ((java.util.Map<?, ?>) authorObj).get("date");
                            if (nameObj != null) author = nameObj.toString();
                            if (dateObj != null) date = dateObj.toString();
                        }
                    }
                    if (sha != null) {
                        java.util.Map<String, Object> item = new java.util.HashMap<>();
                        item.put("id", sha);
                        item.put("shortId", sha.substring(0, Math.min(8, sha.length())));
                        item.put("message", message);
                        item.put("author", author);
                        item.put("date", date);
                        item.put("htmlUrl", htmlUrl);
                        item.put("branch", branch);
                        list.add(item);
                    }
                }
            }
            return Result.ok(list);
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.warn("获取提交列表失败: {}", e.getMessage());
            return Result.error("获取提交列表失败: " + e.getStatusCode());
        } catch (Exception e) {
            log.error("获取提交列表异常", e);
            return Result.error("获取提交列表异常: " + e.getMessage());
        }
    }

    @Operation(summary = "获取当前用户权限范围内的Git前缀（当前支持GitHub）")
    @GetMapping("/prefixes")
    public Result<Map<String, Object>> getPrefixes(@RequestParam(value = "token", required = false) String token,
                                                   jakarta.servlet.http.HttpServletRequest request) {
        try {
            String resolvedToken = resolveUserToken(request, token);
             HttpHeaders headers = new HttpHeaders();
             headers.set("Accept", "application/vnd.github+json");
             if (resolvedToken != null && !resolvedToken.trim().isEmpty()) {
             headers.set("Authorization", "Bearer " + resolvedToken);
             }
             HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 获取用户信息
            ResponseEntity<Map> userResp = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity, Map.class);
            String login = userResp.getBody() != null && userResp.getBody().get("login") != null ? userResp.getBody().get("login").toString() : null;

            java.util.List<String> prefixes = new java.util.ArrayList<>();
            if (login != null) {
                prefixes.add("https://github.com/" + login);
            }
            // 获取用户所属组织
            try {
                ResponseEntity<Object[]> orgResp = restTemplate.exchange("https://api.github.com/user/orgs", HttpMethod.GET, entity, Object[].class);
                Object[] orgs = orgResp.getBody();
                if (orgs != null) {
                    for (Object o : orgs) {
                        if (o instanceof Map) {
                            Object loginObj = ((Map<?, ?>) o).get("login");
                            if (loginObj != null) {
                                prefixes.add("https://github.com/" + loginObj.toString());
                            }
                        }
                    }
                }
            } catch (HttpClientErrorException e) {
                log.warn("获取组织信息失败: {}", e.getMessage());
            }

            Map<String, Object> data = new HashMap<>();
            data.put("prefixes", prefixes);
            return Result.ok(data);
        } catch (HttpClientErrorException e) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("prefixes", new java.util.ArrayList<>());
            return Result.ok("获取Git前缀失败: " + e.getStatusCode(), empty);
        } catch (Exception e) {
            log.error("获取Git前缀异常", e);
            Map<String, Object> empty = new HashMap<>();
            empty.put("prefixes", new java.util.ArrayList<>());
            return Result.ok("获取Git前缀异常: " + e.getMessage(), empty);
        }
    }

    private Result<Map<String, Object>> createGithubRepo(GitRepoCreateRequest req, String token) {
        try {
            String cleaned = req.getGitUrl()
                    .replace("https://github.com/", "")
                    .replace("http://github.com/", "")
                    .replace(".git", "");
            String[] parts = cleaned.split("/");
            if (parts.length < 2) {
                return Result.error("gitUrl格式不正确，需包含所有者和仓库名，如 https://github.com/{owner}/{repo}");
            }
            String owner = parts[0];
            String repoName = parts[1];

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/vnd.github+json");
            if (token != null && !token.trim().isEmpty()) {
                headers.set("Authorization", "Bearer " + token.trim());
            }
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("name", repoName);
            boolean isPrivate = !"public".equalsIgnoreCase(req.getVisibility());
            body.put("private", isPrivate);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 优先尝试在组织下创建
            String orgUrl = "https://api.github.com/orgs/" + owner + "/repos";
            try {
                ResponseEntity<Map> resp = restTemplate.exchange(orgUrl, HttpMethod.POST, entity, Map.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> result = new HashMap<>();
                    Object cloneUrl = resp.getBody() != null ? resp.getBody().get("clone_url") : null;
                    result.put("repoUrl", cloneUrl != null ? cloneUrl.toString() : req.getGitUrl());
                    return Result.ok(result);
                }
            } catch (HttpClientErrorException e) {
                // 如果组织创建失败，尝试在当前用户下创建
                log.warn("组织创建失败，尝试用户下创建: {}", e.getMessage());
            }

            String userUrl = "https://api.github.com/user/repos";
            ResponseEntity<Map> resp = restTemplate.exchange(userUrl, HttpMethod.POST, entity, Map.class);
            if (resp.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = new HashMap<>();
                Object cloneUrl = resp.getBody() != null ? resp.getBody().get("clone_url") : null;
                result.put("repoUrl", cloneUrl != null ? cloneUrl.toString() : req.getGitUrl());
                return Result.ok(result);
            }

            return Result.error("创建GitHub仓库失败: " + resp.getStatusCode());
        } catch (HttpClientErrorException e) {
            return Result.error("创建GitHub仓库失败: " + e.getStatusCode() + " " + e.getMessage());
        } catch (Exception e) {
            log.error("创建GitHub仓库异常", e);
            return Result.error("创建GitHub仓库异常: " + e.getMessage());
        }
    }

    /**
     * 支持解析 https://github.com/{owner}/{repo} 或 git@github.com:{owner}/{repo}.git
     */
    private static String[] parseOwnerRepo(String gitUrl) {
        if (gitUrl == null) return null;
        String url = gitUrl.trim();
        try {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // e.g. https://github.com/owner/repo 或 https://github.com/owner/repo.git
                java.net.URI uri = java.net.URI.create(url);
                String path = uri.getPath(); // /owner/repo(.git)
                if (path != null) {
                    String[] parts = path.replaceFirst("^/", "").split("/");
                    if (parts.length >= 2) {
                        String owner = parts[0];
                        String repo = parts[1].replaceAll("\\.git$", "");
                        return new String[]{owner, repo};
                    }
                }
            } else if (url.startsWith("git@")) {
                // e.g. git@github.com:owner/repo.git
                int colon = url.indexOf(":");
                if (colon > 0 && colon + 1 < url.length()) {
                    String path = url.substring(colon + 1);
                    String[] parts = path.split("/");
                    if (parts.length >= 2) {
                        String owner = parts[0];
                        String repo = parts[1].replaceAll("\\.git$", "");
                        return new String[]{owner, repo};
                    }
                }
            }
        } catch (Exception ignored) { }
        return null;
    }
}