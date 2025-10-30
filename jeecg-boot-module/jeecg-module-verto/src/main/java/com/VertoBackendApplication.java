package com.verto;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Verto后端服务启动类
 * 
 * @author Verto Team
 * @since 2024-01-01
 */
@SpringBootApplication
@MapperScan("com.verto.modules.**.mapper")
public class VertoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VertoBackendApplication.class, args);
    }

}