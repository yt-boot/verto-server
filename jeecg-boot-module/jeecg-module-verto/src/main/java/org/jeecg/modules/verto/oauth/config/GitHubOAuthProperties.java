package org.jeecg.modules.verto.oauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "verto.oauth.github")
public class GitHubOAuthProperties {
    
    /** GitHub OAuth App Client ID */
    private String clientId;
    
    /** GitHub OAuth App Client Secret */
    private String clientSecret;
    
    /** 回调地址 */
    private String redirectUri;
    
    /** 授权范围 */
    private String scope = "user:email";
}