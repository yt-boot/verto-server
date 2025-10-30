package com.verto.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * MyBatis-Plus 配置类
 * 配置分页插件和其他拦截器
 * 
 * @author Verto Team
 * @since 2024-01-27
 */
@Slf4j
@Configuration
public class MybatisPlusConfig {

    @Autowired
    private DataSource dataSource;

    /**
     * 配置 MyBatis-Plus 拦截器
     * 包含分页插件和乐观锁插件
     * 
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 获取数据库类型
        DbType dbType = null;
        try {
            dbType = JdbcUtils.getDbType(dataSource.getConnection().getMetaData().getURL());
            log.info("当前数据库类型: {}", dbType);
        } catch (SQLException e) {
            log.error("获取数据库类型失败", e);
        }
        
        // 添加分页插件
        if (dbType != null && (dbType == DbType.SQL_SERVER || dbType == DbType.SQL_SERVER2005)) {
            // 如果是SQL Server则使用2005分页方式
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.SQL_SERVER2005));
        } else {
            // 其他数据库使用默认分页方式
            interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        }
        
        // 添加乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }
}