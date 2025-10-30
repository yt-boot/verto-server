package com.verto.modules.appmanage.service;

import com.verto.modules.appmanage.entity.AppStatistics;

/**
 * 应用统计数据服务接口
 * 
 * @author Verto
 * @since 2024-10-11
 */
public interface IAppStatisticsService {

    /**
     * 根据应用ID获取统计数据
     * 
     * @param appId 应用ID
     * @return 应用统计数据
     */
    AppStatistics getStatisticsByAppId(String appId);

    /**
     * 获取应用的项目数量
     * 
     * @param appId 应用ID
     * @return 项目数量
     */
    Integer getProjectCountByAppId(String appId);

    /**
     * 获取应用的代码提交次数
     * 
     * @param appId 应用ID
     * @return 代码提交次数
     */
    Integer getCommitCountByAppId(String appId);

    /**
     * 获取应用的部署次数
     * 
     * @param appId 应用ID
     * @return 部署次数
     */
    Integer getDeployCountByAppId(String appId);
}