package com.verto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "github.oauth")
public class GitHubOAuthProperties {
    /** GitHub OAuth App Client ID */
    private String clientId;
    /** GitHub OAuth App Client Secret */
    private String clientSecret;
    /** Redirect URI configured in GitHub OAuth App */
    private String redirectUri;
    /** Scopes to request, comma or space separated */
    private String scope = "read:user user:email repo";

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}