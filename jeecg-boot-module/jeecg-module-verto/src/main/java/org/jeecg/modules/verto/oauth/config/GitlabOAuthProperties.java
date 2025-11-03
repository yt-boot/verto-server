package org.jeecg.modules.verto.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "verto.oauth.gitlab")
public class GitlabOAuthProperties {
    /** 应用的 Client ID */
    private String clientId;
    /** 应用的 Client Secret */
    private String clientSecret;
    /** 回调地址 */
    private String redirectUri;
    /** 授权范围，默认：read_user */
    private String scope = "read_user";
    /** GitLab 服务器地址，默认 https://gitlab.com */
    private String server = "https://gitlab.com";
}