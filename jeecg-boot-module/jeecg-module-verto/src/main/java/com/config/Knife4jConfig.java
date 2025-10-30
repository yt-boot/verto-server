package com.verto.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 用于生成API文档
 * 
 * @author Verto Team
 * @since 2024-01-01
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI信息
     * 
     * @return OpenAPI配置
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Verto Backend API")
                        .description("Verto后端服务接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Verto Team")
                                .email("verto@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}