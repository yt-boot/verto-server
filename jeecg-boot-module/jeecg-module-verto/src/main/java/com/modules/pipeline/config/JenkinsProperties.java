package com.verto.modules.pipeline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsProperties {
    /** Jenkins 基础URL，例如：http://localhost:8080 */
    private String url;
    /** Jenkins 用户名 */
    private String user;
    /** Jenkins API Token */
    private String token;
}