package org.jeecg.modules.verto.oauth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.oauth.config.GitlabOAuthProperties;
import org.jeecg.modules.verto.oauth.config.GitHubOAuthProperties;
import org.jeecg.modules.verto.oauth.entity.OAuthToken;
import org.jeecg.modules.verto.oauth.entity.OAuthUser;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * 第三方登录（GitLab/GitHub）渲染与回调
 * 对齐前端 /sys/thirdLogin/render/gitlab 和 /sys/thirdLogin/render/github 的调用
 */
@RestController
@RequestMapping("/sys/thirdLogin")
@RequiredArgsConstructor
public class VertoThirdLoginController {

    private final GitlabOAuthProperties gitlabProps;
    private final GitHubOAuthProperties githubProps;
    private final IOAuthService oauthService;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Step 1: Redirect to GitLab OAuth authorize page
     */
    @GetMapping("/render/gitlab")
    public ResponseEntity<Void> gitlabAuthorize(@RequestParam(value = "return", required = false) String returnUrl,
                                               HttpServletResponse response) {
        String clientId = gitlabProps.getClientId();
        String redirectUri = gitlabProps.getRedirectUri();
        String scope = gitlabProps.getScope();
        String server = gitlabProps.getServer();
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(redirectUri)) {
            return ResponseEntity.status(500).build();
        }
        String state = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("verto_oauth_state", state);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
        if (StringUtils.hasText(returnUrl)) {
            Cookie ret = new Cookie("verto_oauth_return", returnUrl);
            ret.setHttpOnly(true);
            ret.setPath("/");
            ret.setMaxAge(5 * 60);
            response.addCookie(ret);
        }
        String authorizeUrl = server + "/oauth/authorize"
                + "?client_id=" + urlEnc(clientId)
                + "&redirect_uri=" + urlEnc(redirectUri)
                + "&response_type=code"
                + "&scope=" + urlEnc(scope)
                + "&state=" + urlEnc(state);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(authorizeUrl));
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * Step 2: Callback - exchange code for token, fetch user info, persist, and postMessage to opener
     */
    // Use a unique callback path to avoid conflict with System's generic "/{source}/callback" mapping
    @GetMapping("/gitlab/vertocallback")
    public ResponseEntity<String> gitlabCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpServletRequest request,
            HttpServletResponse response) {
        // Validate state
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
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>授权校验失败（state不匹配），窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(400).contentType(MediaType.TEXT_HTML).body(htmlInvalid);
        }
        if (!StringUtils.hasText(code)) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body>" +
                    "<script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>GitLab授权失败或异常，窗口将自动关闭。</p>" +
                    "</body></html>";
            return ResponseEntity.status(400).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (StringUtils.hasText(error)) {
            return ResponseEntity.status(400).body("OAuth error: " + error);
        }
        // Exchange code for access_token
        String tokenUrl = gitlabProps.getServer() + "/oauth/token";
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", gitlabProps.getClientId());
        form.add("client_secret", gitlabProps.getClientSecret());
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", gitlabProps.getRedirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        Map tokenResp;
        try {
            tokenResp = restTemplate.postForObject(tokenUrl, entity, Map.class);
        } catch (Exception ex) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>交换 access_token 失败，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (tokenResp == null || !tokenResp.containsKey("access_token")) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>未能获取 access_token，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        String accessToken = (String) tokenResp.get("access_token");
        String tokenType = (String) tokenResp.getOrDefault("token_type", "Bearer");
        String scope = (String) tokenResp.getOrDefault("scope", "");

        // Fetch user info
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", tokenType + " " + accessToken);
        authHeaders.set("Accept", "application/json");
        HttpEntity<Void> userEntity = new HttpEntity<>(authHeaders);
        Map userResp;
        try {
            userResp = restTemplate.exchange(gitlabProps.getServer() + "/api/v4/user",
                    org.springframework.http.HttpMethod.GET, userEntity, Map.class).getBody();
        } catch (Exception ex) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>获取用户信息失败，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (userResp == null || !userResp.containsKey("id")) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>未能获取到有效的用户信息，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        String oauthUserId = String.valueOf(userResp.get("id"));
        String login = String.valueOf(userResp.get("username"));
        String name = String.valueOf(userResp.getOrDefault("name", ""));
        String avatarUrl = String.valueOf(userResp.getOrDefault("avatar_url", ""));
        String email = String.valueOf(userResp.getOrDefault("email", ""));

        // Persist
        OAuthUser user = oauthService.upsertUser("gitlab", oauthUserId, login, name, avatarUrl, email);
        OAuthToken token = oauthService.saveToken("gitlab", oauthUserId, accessToken, tokenType, scope);

        // 返回与前端约定的消息格式："绑定手机号,{uuid}" 用于触发绑定
        String html = "<!DOCTYPE html>" +
                "<html><head><meta charset=\"utf-8\"><title>GitLab Bind Success</title></head>" +
                "<body>" +
                "<script>" +
                "if (window.opener) {" +
                "  window.opener.postMessage('绑定手机号," + jsEnc(oauthUserId) + "', '*');" +
                "}" +
                "window.close();" +
                "</script>" +
                "<p>GitLab账号绑定成功，窗口将自动关闭。</p>" +
                "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    /**
     * GitHub OAuth authorize redirect
     */
    @GetMapping("/render/github")
    public ResponseEntity<Void> githubAuthorize(@RequestParam(value = "return", required = false) String returnUrl,
                                                HttpServletResponse response) {
        String clientId = githubProps.getClientId();
        String redirectUri = githubProps.getRedirectUri();
        String scope = githubProps.getScope();
        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(redirectUri)) {
            return ResponseEntity.status(500).build();
        }
        String state = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("verto_oauth_state", state);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(5 * 60);
        response.addCookie(cookie);
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
                + "&response_type=code"
                + "&scope=" + urlEnc(scope)
                + "&state=" + urlEnc(state);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(authorizeUrl));
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * GitHub OAuth callback: exchange code for token, fetch user info, persist and notify via postMessage
     */
    @GetMapping("/github/vertocallback")
    public ResponseEntity<String> githubCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "error", required = false) String error,
            HttpServletRequest request,
            HttpServletResponse response) {
        // Validate state
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
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>授权校验失败（state不匹配），窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(400).contentType(MediaType.TEXT_HTML).body(htmlInvalid);
        }
        if (!StringUtils.hasText(code)) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body>" +
                    "<script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
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
        form.add("client_id", githubProps.getClientId());
        form.add("client_secret", githubProps.getClientSecret());
        form.add("code", code);
        form.add("redirect_uri", githubProps.getRedirectUri());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Accept", "application/json");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        Map tokenResp;
        try {
            tokenResp = restTemplate.postForObject(tokenUrl, entity, Map.class);
        } catch (Exception ex) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>交换 access_token 失败，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (tokenResp == null || !tokenResp.containsKey("access_token")) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>未能获取 access_token，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        String accessToken = (String) tokenResp.get("access_token");
        String tokenType = String.valueOf(tokenResp.getOrDefault("token_type", "Bearer"));
        String scope = String.valueOf(tokenResp.getOrDefault("scope", githubProps.getScope()));

        // Fetch user info from GitHub
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Authorization", tokenType + " " + accessToken);
        authHeaders.set("Accept", "application/json");
        HttpEntity<Void> userEntity = new HttpEntity<>(authHeaders);
        Map userResp;
        try {
            userResp = restTemplate.exchange("https://api.github.com/user",
                    org.springframework.http.HttpMethod.GET, userEntity, Map.class).getBody();
        } catch (Exception ex) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>获取用户信息失败，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        if (userResp == null || !userResp.containsKey("id")) {
            String html = "<!DOCTYPE html>" +
                    "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Failed</title></head>" +
                    "<body><script>if (window.opener) {window.opener.postMessage('登录失败', '*');} window.close();</script>" +
                    "<p>未能获取到有效的用户信息，窗口将自动关闭。</p></body></html>";
            return ResponseEntity.status(500).contentType(MediaType.TEXT_HTML).body(html);
        }
        String oauthUserId = String.valueOf(userResp.get("id"));
        String login = String.valueOf(userResp.get("login"));
        String name = String.valueOf(userResp.getOrDefault("name", ""));
        String avatarUrl = String.valueOf(userResp.getOrDefault("avatar_url", ""));
        String email = String.valueOf(userResp.getOrDefault("email", ""));

        // Persist via IOAuthService
        OAuthUser user = oauthService.upsertUser("github", oauthUserId, login, name, avatarUrl, email);
        OAuthToken token = oauthService.saveToken("github", oauthUserId, accessToken, tokenType, scope);

        String html = "<!DOCTYPE html>" +
                "<html><head><meta charset=\"utf-8\"><title>GitHub Bind Success</title></head>" +
                "<body>" +
                "<script>" +
                "if (window.opener) {" +
                "  window.opener.postMessage('绑定手机号," + jsEnc(oauthUserId) + "', '*');" +
                "}" +
                "window.close();" +
                "</script>" +
                "<p>GitHub账号绑定成功，窗口将自动关闭。</p>" +
                "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }


    private static String urlEnc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
    private static String jsEnc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("'", "\\''");
    }

    // ====== Uppercase route aliases for frontend compatibility ======
    /**
     * Alias: /render/GITHUB -> reuse githubAuthorize
     */
    @GetMapping("/render/GITHUB")
    public ResponseEntity<Void> githubAuthorizeUpper(@RequestParam(value = "return", required = false) String returnUrl,
                                                     HttpServletResponse response) {
        return githubAuthorize(returnUrl, response);
    }

    /**
     * Alias: /GITHUB/vertocallback -> reuse githubCallback
     */
    @GetMapping("/GITHUB/vertocallback")
    public ResponseEntity<String> githubCallbackUpper(@RequestParam(value = "code", required = false) String code,
                                                      @RequestParam(value = "state", required = false) String state,
                                                      @RequestParam(value = "error", required = false) String error,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        return githubCallback(code, state, error, request, response);
    }

    /**
     * Alias: /render/GITLAB -> reuse gitlabAuthorize
     */
    @GetMapping("/render/GITLAB")
    public ResponseEntity<Void> gitlabAuthorizeUpper(@RequestParam(value = "return", required = false) String returnUrl,
                                                     HttpServletResponse response) {
        return gitlabAuthorize(returnUrl, response);
    }

    /**
     * Alias: /GITLAB/vertocallback -> reuse gitlabCallback
     */
    @GetMapping("/GITLAB/vertocallback")
    public ResponseEntity<String> gitlabCallbackUpper(@RequestParam(value = "code", required = false) String code,
                                                      @RequestParam(value = "state", required = false) String state,
                                                      @RequestParam(value = "error", required = false) String error,
                                                      HttpServletRequest request,
                                                      HttpServletResponse response) {
        return gitlabCallback(code, state, error, request, response);
    }

}
