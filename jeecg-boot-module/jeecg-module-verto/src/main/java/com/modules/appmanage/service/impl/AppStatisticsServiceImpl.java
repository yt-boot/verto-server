package com.verto.modules.appmanage.service.impl;

import com.verto.modules.appmanage.entity.AppStatistics;
import com.verto.modules.appmanage.service.IAppStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 应用统计数据服务实现类
 * 
 * @author Verto
 * @since 2024-10-11
 */
@Slf4j
@Service
public class AppStatisticsServiceImpl implements IAppStatisticsService {

    /**
     * 模拟统计数据
     */
    private static final Map<String, AppStatistics> MOCK_STATISTICS = new HashMap<>();

    static {
        // 初始化模拟数据
        MOCK_STATISTICS.put("1", new AppStatistics(12, 1234, 89));
        MOCK_STATISTICS.put("2", new AppStatistics(8, 856, 45));
        MOCK_STATISTICS.put("3", new AppStatistics(15, 2156, 123));
        MOCK_STATISTICS.put("4", new AppStatistics(6, 432, 28));
        MOCK_STATISTICS.put("5", new AppStatistics(20, 3456, 178));
    }

    /**
     * 根据应用ID获取统计数据
     * 
     * @param appId 应用ID
     * @return 应用统计数据
     */
    @Override
    public AppStatistics getStatisticsByAppId(String appId) {
        log.info("获取应用统计数据，应用ID: {}", appId);
        
        // 优先返回模拟数据
        AppStatistics mockData = MOCK_STATISTICS.get(appId);
        if (mockData != null) {
            log.info("返回模拟统计数据: {}", mockData);
            return mockData;
        }
        
        // 如果没有模拟数据，生成随机数据
        AppStatistics randomData = generateRandomStatistics(appId);
        log.info("生成随机统计数据: {}", randomData);
        return randomData;
    }

    /**
     * 获取应用的项目数量
     * 
     * @param appId 应用ID
     * @return 项目数量
     */
    @Override
    public Integer getProjectCountByAppId(String appId) {
        // 这里可以实现真实的项目数量查询逻辑
        // 例如：从项目管理表中查询该应用关联的项目数量
        AppStatistics statistics = getStatisticsByAppId(appId);
        return statistics.getProjectCount();
    }

    /**
     * 获取应用的代码提交次数
     * 
     * @param appId 应用ID
     * @return 代码提交次数
     */
    @Override
    public Integer getCommitCountByAppId(String appId) {
        // 这里可以实现真实的代码提交次数查询逻辑
        // 例如：通过Git API查询该应用仓库的提交次数
        AppStatistics statistics = getStatisticsByAppId(appId);
        return statistics.getCommitCount();
    }

    /**
     * 获取应用的部署次数
     * 
     * @param appId 应用ID
     * @return 部署次数
     */
    @Override
    public Integer getDeployCountByAppId(String appId) {
        // 这里可以实现真实的部署次数查询逻辑
        // 例如：从部署记录表中查询该应用的部署次数
        AppStatistics statistics = getStatisticsByAppId(appId);
        return statistics.getDeployCount();
    }

    /**
     * 生成随机统计数据
     * 
     * @param appId 应用ID
     * @return 随机统计数据
     */
    private AppStatistics generateRandomStatistics(String appId) {
        Random random = new Random();
        
        // 基于应用ID生成相对稳定的随机数
        int seed = appId.hashCode();
        random.setSeed(seed);
        
        int projectCount = random.nextInt(20) + 1; // 1-20个项目
        int commitCount = random.nextInt(2000) + 100; // 100-2100次提交
        int deployCount = random.nextInt(150) + 10; // 10-160次部署
        
        return new AppStatistics(projectCount, commitCount, deployCount);
    }
}