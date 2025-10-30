package com.verto.modules.appmanage.entity;

import lombok.Data;
import java.util.Map;

/**
 * Package.json信息实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
public class PackageJsonInfo {
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目版本
     */
    private String version;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 生产依赖
     */
    private Map<String, String> dependencies;
    
    /**
     * 开发依赖
     */
    private Map<String, String> devDependencies;
    
    /**
     * 对等依赖
     */
    private Map<String, String> peerDependencies;
    
    /**
     * 可选依赖
     */
    private Map<String, String> optionalDependencies;
    
    /**
     * 脚本命令
     */
    private Map<String, String> scripts;
    
    /**
     * 关键词
     */
    private String[] keywords;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 许可证
     */
    private String license;
    
    /**
     * 仓库信息
     */
    private String repository;
    
    /**
     * 主页
     */
    private String homepage;
    
    /**
     * 问题反馈地址
     */
    private String bugs;
}