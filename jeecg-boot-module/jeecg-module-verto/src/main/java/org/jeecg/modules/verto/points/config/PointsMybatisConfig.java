package org.jeecg.modules.verto.points.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "org.jeecg.modules.verto.points.mapper")
public class PointsMybatisConfig {
}