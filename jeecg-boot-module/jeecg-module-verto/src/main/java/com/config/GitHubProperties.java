package com.verto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github.api")
public class GitHubProperties {
    /** GitHub API Base URL */
    private String baseUrl = "https://api.github.com";
    /** Personal Access Token (建议通过环境变量注入) */
    private String token;
    /** 认证方案：token 或 Bearer */
    private String authScheme = "token";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAuthScheme() {
        return authScheme;
    }

    public void setAuthScheme(String authScheme) {
        this.authScheme = authScheme;
    }
}