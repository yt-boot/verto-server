package com.verto.modules.oauth.controller;

import com.verto.config.GitHubOAuthProperties;
import com.verto.modules.oauth.entity.OAuthToken;
import com.verto.modules.oauth.entity.OAuthUser;
import com.verto.modules.oauth.service.OAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.verto.common.api.vo.Result;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final GitHubOAuthProperties props;
    private final OAuthService oauthService;
    private final RestTemplate githubRestTemplate;

    public OAuthController(GitHubOAuthProperties props, OAuthService oauthService, RestTemplate githubRestTemplate) {
        this.props = props;
        this.oauthService = oauthService;
        this.githubRestTemplate = githubRestTemplate;
    }

    /**
     * Step 1: Redirect to GitHub OAuth authorize page
     */
    @GetMapping("/github/authorize")
    public ResponseEntity<Void> githubAuthorize(@RequestParam(value = "return", required = false) String returnUrl, HttpServletResponse response) {
        return buildGithubAuthorizeRedirect(returnUrl, response);
    }

    /**
     * Alias endpoint to align with Jeecg's explicit render/github style
     * Reference: ThirdLoginController#renderGithub
     */
    @GetMapping("/github/render")
    public ResponseEntity<Void> githubRender(@RequestParam(value = "return", required = false) String returnUrl, HttpServletResponse response) {
        return buildGithubAuthorizeRedirect(returnUrl, response);
    }

    private ResponseEntity<Void> buildGithubAuthorizeRedirect(String returnUrl, HttpServletResponse response) {
        String clientId = props.getClientId();
        String redirectUri = props.getRedirectUri();
        String scope = props.getScope();
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(redirectUri)) {
            return ResponseEntity.status(500).build();
        }
        String state = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("verto_oauth_state", state);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // Valid for 5 minutes
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
        // Optionally store return url for later use
        if (StringUtils.hasText(returnUrl)) {
            Cookie ret = new Cookie("verto_oauth_return", returnUrl);
            ret.setHttpOnly(true);
            ret.setPath("/");
            ret.setMaxAge(5 * 60);
            response.addCookie(ret);
        }
        String authorizeUrl = "https://github.com/login/oauth/authorize"
                + "?client_id=" + urlEnc(clientId)
                + "&redirect_uri=" + urlEnc(redirectUri)
                + "&scope=" + urlEnc(scope)
                + "&state=" + urlEnc(state);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(authorizeUrl));
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * Step 2: Callback - exchange code for token, fetch user info, persist, set cookie, and postMessage to opener
     */
    @GetMapping("/github/callback")
    public ResponseEntity<String> githubCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpServletRequest request,
            HttpServletResponse response) {
        // Validate state to prevent CSRF, similar to Jeecg's AuthStateUtils usage
        String cookieState = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("verto_oauth_state".equals(c.getName())) {
                    cookieState = c.getValue();
                    break;
                }
            }
        }
        if (StringUtils.hasText(state) && StringUtils.hasText(cookieState) && !state.equals(cookieState)) {
            String htmlInvalid = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage({vertoOAuth:true, platform:'github', error:'invalid_state'}, '*');} window.close();</script>" +
                    "<p>授权校验失败（state不匹配），窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(400).contentType(MediaType.TEXT_HTML).body(htmlInvalid);
        }
        // Gracefully handle missing code (user canceled or accessed callback directly)
        if (!StringUtils.hasText(code)) {
            String err = StringUtils.hasText(error) ? error : "missing_code";
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body>" +
                    "<script>" +
                    "if (window.opener) {" +
                    "  window.opener.postMessage({vertoOAuth:true, platform:'github', error:'" + jsEnc(err) + "'}, '*');" +
                    "}" +
                    "window.close();" +
                    "</script>" +
                    "<p>GitHub授权失败或异常，窗口将自动关闭。</p>" +
                    "</body></html>";
            return ResponseEntity.status(400).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (StringUtils.hasText(error)) {
            return ResponseEntity.status(400).body("OAuth error: " + error);
        }
        // Exchange code for access_token
        String tokenUrl = "https://github.com/login/oauth/access_token";
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", props.getClientId());
        form.add("client_secret", props.getClientSecret());
        form.add("code", code);
        if (StringUtils.hasText(props.getRedirectUri())) {
            form.add("redirect_uri", props.getRedirectUri());
        }
        if (StringUtils.hasText(state)) {
            form.add("state", state);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        Map tokenResp = githubRestTemplate.postForObject(tokenUrl, entity, Map.class);
        if (tokenResp == null || !tokenResp.containsKey("access_token")) {
            return ResponseEntity.status(500).body("Failed to get access_token from GitHub");
        }
        String accessToken = (String) tokenResp.get("access_token");
        String tokenType = (String) tokenResp.getOrDefault("token_type", "Bearer");
        String scope = (String) tokenResp.getOrDefault("scope", "");

        // Fetch user info with the access token
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", tokenType + " " + accessToken);
        authHeaders.set("Accept", "application/vnd.github+json");
        authHeaders.set("User-Agent", "VertoBackend/1.0");
        HttpEntity<Void> userEntity = new HttpEntity<>(authHeaders);
        Map userResp = githubRestTemplate.exchange("https://api.github.com/user", org.springframework.http.HttpMethod.GET, userEntity, Map.class).getBody();
        if (userResp == null || !userResp.containsKey("id")) {
            return ResponseEntity.status(500).body("Failed to fetch user info from GitHub");
        }
        String oauthUserId = String.valueOf(userResp.get("id"));
        String login = String.valueOf(userResp.get("login"));
        String name = String.valueOf(userResp.getOrDefault("name", ""));
        String avatarUrl = String.valueOf(userResp.getOrDefault("avatar_url", ""));
        String email = String.valueOf(userResp.getOrDefault("email", ""));

        // Persist
        OAuthUser user = oauthService.upsertUser("github", oauthUserId, login, name, avatarUrl, email);
        OAuthToken token = oauthService.saveToken("github", oauthUserId, accessToken, tokenType, scope);

        // 不再将 access_token 写入 Cookie。令牌已在后端持久化保存，后续请求由后端代理使用。
        // 如需设置会话标识，请在统一登录模块发放 HttpOnly + Secure + SameSite 的会话 Cookie。

        // Return a small HTML page that notifies opener WITHOUT exposing access_token
        String html = "<!DOCTYPE html>" +
                "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Success</title></head>" +
                "<body>" +
                "<script>" +
                "if (window.opener) {" +
                "  window.opener.postMessage({vertoOAuth:true, platform:'github', uuid:'" + jsEnc(oauthUserId) + "', username:'" + jsEnc(login) + "'}, '*');" +
                "}" +
                "window.close();" +
                "</script>" +
                "<p>GitHub账号绑定成功，窗口将自动关闭。</p>" +
                "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    /**
     * 绑定第三方账号到当前用户
     * 对应前端的 bindThirdAppAccount 接口
     */
    @PostMapping("/bind")
    public Result<String> bindThirdAccount(@RequestParam String thirdUserUuid, @RequestParam String thirdType, HttpServletRequest request) {
        try {
            // 从请求头获取当前用户ID
            String currentUserId = request.getHeader("X-User-Id");
            if (!StringUtils.hasText(currentUserId)) {
                return Result.error("用户身份验证失败，请重新登录");
            }
            
            // 调用服务层进行绑定
            boolean success = oauthService.bindUserAccount(currentUserId, thirdType, thirdUserUuid);
            
            if (success) {
                return Result.OK("第三方账号绑定成功");
            } else {
                return Result.error("第三方账号绑定失败，请稍后重试");
            }
        } catch (Exception e) {
            return Result.error("第三方账号绑定失败：" + e.getMessage());
        }
    }

    /**
     * 解绑第三方账号
     */
    @PostMapping("/unbind")
    public Result<String> unbindThirdAccount(@RequestParam String thirdType, HttpServletRequest request) {
        try {
            // 从请求头获取当前用户ID
            String currentUserId = request.getHeader("X-User-Id");
            if (!StringUtils.hasText(currentUserId)) {
                return Result.error("用户身份验证失败，请重新登录");
            }
            
            // 调用服务层进行解绑
            boolean success = oauthService.unbindUserAccount(currentUserId, thirdType);
            
            if (success) {
                return Result.OK("第三方账号解绑成功");
            } else {
                return Result.error("第三方账号解绑失败，请稍后重试");
            }
        } catch (Exception e) {
            return Result.error("第三方账号解绑失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户绑定的第三方账号信息
     */
    @GetMapping("/user/bindings")
    public Result<Map<String, Object>> getUserBindings(HttpServletRequest request) {
        try {
            // 从请求头获取当前用户ID
            String currentUserId = request.getHeader("X-User-Id");
            if (!StringUtils.hasText(currentUserId)) {
                return Result.error("用户身份验证失败，请重新登录");
            }
            
            // 调用服务层获取绑定信息
            Map<String, Object> bindings = oauthService.getUserBindings(currentUserId);
            
            return Result.OK(bindings);
        } catch (Exception e) {
            return Result.error("获取绑定信息失败：" + e.getMessage());
        }
    }

    private static String urlEnc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
    private static String jsEnc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("'", "\\'");
    }
}