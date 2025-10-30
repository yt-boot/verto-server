package com.verto.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({GitHubProperties.class, GitHubOAuthProperties.class})
public class GitHubApiConfig {

    @Bean("githubRestTemplate")
    public RestTemplate githubRestTemplate(GitHubProperties props) {
        RestTemplate restTemplate = new RestTemplate();

        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            // Always set Accept for GitHub API
            if (!headers.containsKey(HttpHeaders.ACCEPT)) {
                headers.set(HttpHeaders.ACCEPT, "application/vnd.github+json");
            }
            // GitHub requires a User-Agent header
            if (!headers.containsKey(HttpHeaders.USER_AGENT)) {
                headers.set(HttpHeaders.USER_AGENT, "VertoBackend/1.0");
            }
            // If Authorization not already set (e.g., user token provided), use PAT from config
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                String token = props.getToken();
                String scheme = StringUtils.hasText(props.getAuthScheme()) ? props.getAuthScheme() : "token";
                if (StringUtils.hasText(token)) {
                    headers.set(HttpHeaders.AUTHORIZATION, scheme + " " + token);
                }
            }
            return execution.execute(request, body);
        };

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(authInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}