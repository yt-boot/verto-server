package com.verto.modules.appmanage.service;

import com.verto.modules.appmanage.entity.PackageJsonInfo;

/**
 * Package.json信息服务接口
 * 
 * @author verto
 * @since 2024-01-27
 */
public interface IPackageJsonService {
    
    /**
     * 根据应用ID获取package.json信息
     * 
     * @param appId 应用ID
     * @return package.json信息
     */
    PackageJsonInfo getPackageJsonByAppId(String appId);
    
    /**
     * 从Git仓库获取package.json信息
     * 
     * @param gitUrl Git仓库地址
     * @return package.json信息
     */
    PackageJsonInfo getPackageJsonFromGit(String gitUrl);
    
    /**
     * 解析package.json内容
     * 
     * @param packageJsonContent package.json文件内容
     * @return package.json信息
     */
    PackageJsonInfo parsePackageJson(String packageJsonContent);
}