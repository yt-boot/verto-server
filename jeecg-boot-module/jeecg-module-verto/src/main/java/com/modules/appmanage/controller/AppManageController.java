package com.verto.modules.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.appmanage.entity.AppManage;
import com.verto.modules.appmanage.entity.AppStatistics;
import com.verto.modules.appmanage.entity.PackageJsonInfo;
import com.verto.modules.appmanage.entity.AppGitRepoInfo;
import com.verto.modules.appmanage.service.IAppManageService;
import com.verto.modules.appmanage.service.IAppStatisticsService;
import com.verto.modules.appmanage.service.IPackageJsonService;
import com.verto.modules.appmanage.service.IAppGitRepoInfoService;
import com.verto.modules.oauth.service.OAuthService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 应用管理控制器
 * 
 * @author verto
 * @since 2024-01-27
 */
@Tag(name = "应用管理", description = "应用管理相关接口")
@RestController
@RequestMapping("/appmanage/app")
@Slf4j
public class AppManageController {

    @Autowired
    private IAppManageService appManageService;

    @Autowired
    private IPackageJsonService packageJsonService;

    @Autowired
    private IAppStatisticsService appStatisticsService;

    @Autowired
    private IAppGitRepoInfoService appGitRepoInfoService;

    @Autowired
    @Qualifier("githubRestTemplate")
    private RestTemplate githubRestTemplate;

    @Autowired
    private OAuthService oauthService;

    // 解析当前系统用户绑定的 GitHub 访问令牌（优先从 X-User-Id 获取用户ID）
    private String resolveUserToken(HttpServletRequest request) {
        if (request == null) return null;
        try {
            String currentUserId = request.getHeader("X-User-Id");
            if (!StringUtils.hasText(currentUserId)) {
                return null;
            }
            String dbToken = oauthService.getAccessTokenForSystemUser(currentUserId, "github");
            if (StringUtils.hasText(dbToken)) {
                return dbToken.trim();
            }
            // Fallback（仅开发调试用途）：若数据库未取到绑定令牌，则尝试从 Cookie 中读取 verto_github_token
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("verto_github_token".equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                        log.warn("未从数据库取到用户GitHub令牌，使用Cookie verto_github_token 作为临时令牌，userId={}", currentUserId);
                        return c.getValue().trim();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析用户GitHub令牌失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 分页查询应用列表
     * 
     * @param appManage 查询条件
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询应用列表")
    @GetMapping(value = "/list")
    public Result<IPage<AppManage>> queryPageList(AppManage appManage,
                                                  @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<AppManage> queryWrapper = new QueryWrapper<>();
        
        // 应用名称模糊查询
        if (appManage.getAppName() != null && !appManage.getAppName().trim().isEmpty()) {
            queryWrapper.like("app_name", appManage.getAppName());
        }
        
        // 应用领域精确查询
        if (appManage.getDomain() != null && !appManage.getDomain().trim().isEmpty()) {
            queryWrapper.eq("domain", appManage.getDomain());
        }
        
        // 状态查询
        if (appManage.getStatus() != null) {
            queryWrapper.eq("status", appManage.getStatus());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        Page<AppManage> page = new Page<>(pageNo, pageSize);
        IPage<AppManage> pageList = appManageService.page(page, queryWrapper);
        
        return Result.ok(pageList);
    }

    /**
     * 根据ID查询应用详情
     * 
     * @param id 应用ID
     * @return 应用详情
     */
    @Operation(summary = "根据ID查询应用详情")
    @GetMapping(value = "/queryById")
    public Result<AppManage> queryById(@Parameter(description = "应用ID") @RequestParam(name = "id", required = true) String id) {
        AppManage appManage = appManageService.getById(id);
        if (appManage == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(appManage);
    }

    /**
     * 新增应用
     * 
     * @param appManage 应用信息
     * @return 操作结果，包含创建的应用信息
     */
    @Operation(summary = "新增应用")
    @PostMapping(value = "/add")
    public Result<AppManage> add(@RequestBody AppManage appManage) {
        appManage.setCreateTime(new Date());
        appManage.setCreateBy("admin"); // 实际项目中应从当前登录用户获取
        appManageService.save(appManage);
        return Result.ok(appManage);
    }

    /**
     * 编辑应用
     * 
     * @param appManage 应用信息
     * @return 操作结果
     */
    @Operation(summary = "编辑应用")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody AppManage appManage) {
        appManage.setUpdateTime(new Date());
        appManage.setUpdateBy("admin"); // 实际项目中应从当前登录用户获取
        appManageService.updateById(appManage);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除应用
     * 
     * @param id 应用ID
     * @return 操作结果
     */
    @Operation(summary = "删除应用")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@Parameter(description = "应用ID") @RequestParam(name = "id", required = true) String id) {
        appManageService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除应用
     * 
     * @param ids 应用ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除应用")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@Parameter(description = "应用ID列表") @RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        appManageService.removeByIds(idList);
        return Result.ok("批量删除成功!");
    }

    /**
     * 获取应用的package.json信息
     * 
     * @param id 应用ID
     * @return package.json信息
     */
    @Operation(summary = "获取应用的package.json信息")
    @GetMapping(value = "/package-json")
    public Result<PackageJsonInfo> getPackageJson(@Parameter(description = "应用ID") @RequestParam(name = "id", required = true) String id) {
        try {
            PackageJsonInfo packageJsonInfo = packageJsonService.getPackageJsonByAppId(id);
            if (packageJsonInfo != null) {
                return Result.ok(packageJsonInfo);
            } else {
                return Result.error("未找到该应用的 package.json 信息");
            }
        } catch (Exception e) {
            log.error("获取应用package.json信息失败", e);
            return Result.error("获取package.json信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取应用统计数据
     * 
     * @param id 应用ID
     * @return 应用统计数据
     */
    @Operation(summary = "获取应用统计数据")
    @GetMapping(value = "/statistics")
    public Result<AppStatistics> getStatistics(@Parameter(description = "应用ID") @RequestParam(name = "id", required = true) String id) {
        try {
            AppStatistics statistics = appStatisticsService.getStatisticsByAppId(id);
            if (statistics != null) {
                return Result.ok(statistics);
            } else {
                return Result.error("未找到该应用的统计数据");
            }
        } catch (Exception e) {
            log.error("获取应用统计数据失败", e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 同步并持久化 Git 仓库详细信息到应用绑定表（app_git_repo_info），后续同步会更新。
     */
    @Operation(summary = "同步应用的Git仓库详细信息并持久化")
    @PostMapping("/git/sync")
    public Result<AppGitRepoInfo> syncGitRepoInfo(@Parameter(description = "应用ID") @RequestParam(name = "appId", required = true) String appId, HttpServletRequest request) {
        try {
            AppManage appManage = appManageService.getById(appId);
            if (appManage == null) {
                return Result.error("未找到应用");
            }
            String gitUrl = appManage.getGitUrl();
            if (gitUrl == null || gitUrl.trim().isEmpty()) {
                return Result.error("该应用未配置 Git 仓库地址");
            }

            String[] ownerRepo = parseOwnerRepo(gitUrl);
            if (ownerRepo == null) {
                return Result.error("无法解析 Git 仓库地址: " + gitUrl);
            }
            String owner = ownerRepo[0];
            String repo = ownerRepo[1];

            // 根据当前用户解析 GitHub 令牌；若为空则不设置头，交由 PAT 拦截器兜底
            String userToken = resolveUserToken(request);
            HttpHeaders headers = new HttpHeaders();
            // 始终设置 Accept 头
            headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
            if (StringUtils.hasText(userToken)) {
                String tokenVal = userToken.trim();
                String scheme = (tokenVal.startsWith("ghp_") || tokenVal.startsWith("github_pat_")) ? "token" : "Bearer";
                headers.set(HttpHeaders.AUTHORIZATION, scheme + " " + tokenVal);
                log.info("/git/sync 使用GitHub授权方案: {} , userId={}", scheme, request.getHeader("X-User-Id"));
            } else {
                log.info("/git/sync 未解析到用户令牌，准备使用全局PAT（若已配置） , userId={}", request.getHeader("X-User-Id"));
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            try {
                String repoUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);
                ResponseEntity<java.util.Map> repoResp = githubRestTemplate.exchange(repoUrl, HttpMethod.GET, entity, java.util.Map.class);
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> repoInfo = (java.util.Map<String, Object>) repoResp.getBody();
                if (repoInfo == null || repoInfo.isEmpty()) {
                    return Result.error("GitHub 仓库信息获取失败");
                }

                AppGitRepoInfo info = new AppGitRepoInfo();
                info.setAppId(appId);
                info.setOwner(owner);
                info.setRepoName(repo);
                info.setHtmlUrl((String) repoInfo.get("html_url"));
                info.setCloneUrl((String) repoInfo.get("clone_url"));
                info.setSshUrl((String) repoInfo.get("ssh_url"));
                info.setDescription((String) repoInfo.get("description"));
                Object privateObj = repoInfo.get("private");
                info.setVisibility(Boolean.TRUE.equals(privateObj) ? "private" : "public");
                info.setStars(toInt(repoInfo.get("stargazers_count")));
                info.setForks(toInt(repoInfo.get("forks_count")));
                info.setOpenIssues(toInt(repoInfo.get("open_issues_count")));
                Object licenseObj = repoInfo.get("license");
                if (licenseObj instanceof java.util.Map) {
                    info.setLicense((String) ((java.util.Map<?, ?>) licenseObj).get("name"));
                }
                Object topicsObj = repoInfo.get("topics");
                if (topicsObj instanceof java.util.List) {
                    info.setTopics(String.join(",", ((java.util.List<String>) topicsObj)));
                }
                String defaultBranch = (String) repoInfo.get("default_branch");
                info.setDefaultBranch(defaultBranch);
                info.setCreatedAt(parseIsoDatetime((String) repoInfo.get("created_at")));
                info.setUpdatedAt(parseIsoDatetime((String) repoInfo.get("updated_at")));

                // 获取分支数量（仅统计第一页）
                try {
                    String branchesUrl = String.format("https://api.github.com/repos/%s/%s/branches?per_page=100", owner, repo);
                    ResponseEntity<java.util.List> branchesResp = githubRestTemplate.exchange(branchesUrl, HttpMethod.GET, entity, java.util.List.class);
                    java.util.List<?> branches = branchesResp.getBody();
                    if (branches != null) {
                        info.setBranchCount(branches.size());
                    }
                } catch (Exception e1) {
                    log.warn("获取分支列表失败: {}", e1.getMessage());
                }

                // 获取默认分支最新提交
                try {
                    if (defaultBranch != null && !defaultBranch.isEmpty()) {
                        String commitsUrl = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s&per_page=1", owner, repo, defaultBranch);
                        ResponseEntity<java.util.List> commitsResp = githubRestTemplate.exchange(commitsUrl, HttpMethod.GET, entity, java.util.List.class);
                        java.util.List<?> commits = commitsResp.getBody();
                        if (commits != null && !commits.isEmpty()) {
                            Object first = commits.get(0);
                            if (first instanceof java.util.Map) {
                                java.util.Map<?, ?> commitMap = (java.util.Map<?, ?>) first;
                                info.setLastCommitSha((String) commitMap.get("sha"));
                                Object commitInner = commitMap.get("commit");
                                if (commitInner instanceof java.util.Map) {
                                    java.util.Map<?, ?> inner = (java.util.Map<?, ?>) commitInner;
                                    info.setLastCommitMessage((String) inner.get("message"));
                                    Object committerObj = inner.get("committer");
                                    if (committerObj instanceof java.util.Map) {
                                        java.util.Map<?, ?> committerMap = (java.util.Map<?, ?>) committerObj;
                                        info.setLastCommitter((String) committerMap.get("name"));
                                        info.setLastCommitTime(parseIsoDatetime((String) committerMap.get("date")));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e2) {
                    log.warn("获取最新提交失败: {}", e2.getMessage());
                }

                info.setLastSyncedAt(java.time.LocalDateTime.now());
                boolean ok = appGitRepoInfoService.upsertByAppId(info);
                if (!ok) {
                    return Result.error("持久化 Git 仓库信息失败");
                }
                return Result.ok(info);
            } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized uex) {
                // 用户令牌或PAT无效
                String hint = "GitHub凭证无效，请先在【设置-账号绑定】绑定GitHub或更新PAT（非必填）。";
                return Result.error("同步失败: 401 Unauthorized - " + hint);
            }
        } catch (Exception e) {
            log.error("同步Git仓库信息失败", e);
            return Result.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 获取已持久化的 Git 仓库信息（不触发同步），用于前端页面初始化展示。
     *
     * @param appId 应用ID
     * @return app_git_repo_info 记录
     */
    @Operation(summary = "查询应用的Git仓库信息（已持久化）")
    @GetMapping("/git/info")
    public Result<AppGitRepoInfo> getGitRepoInfo(@Parameter(description = "应用ID") @RequestParam(name = "appId", required = true) String appId) {
        try {
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AppGitRepoInfo> qw = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            qw.eq("app_id", appId);
            AppGitRepoInfo info = appGitRepoInfoService.getOne(qw, false);
            if (info == null) {
                return Result.error("未找到该应用的 Git 仓库信息");
            }
            return Result.ok(info);
        } catch (Exception e) {
            log.error("查询应用Git仓库信息失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    private static Integer toInt(Object o) {
        try {
            if (o == null) return null;
            if (o instanceof Number) return ((Number) o).intValue();
            return Integer.parseInt(String.valueOf(o));
        } catch (Exception e) {
            return null;
        }
    }

    private static java.time.LocalDateTime parseIsoDatetime(String s) {
        try {
            if (s == null || s.isEmpty()) return null;
            // GitHub timestamps are ISO_INSTANT (e.g., 2024-01-01T12:00:00Z)
            java.time.Instant instant = java.time.Instant.parse(s);
            return java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());
        } catch (Exception e) {
            return null;
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
                // e.g. https://github.com/owner/repo or https://github.com/owner/repo.git
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