package com.verto.modules.appmanage.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 应用统计数据实体类
 * 
 * @author Verto
 * @since 2024-10-11
 */
@Data
@Schema(description = "应用统计数据")
public class AppStatistics {

    /**
     * 项目数量
     */
    @Schema(description = "项目数量")
    private Integer projectCount;

    /**
     * 代码提交次数
     */
    @Schema(description = "代码提交次数")
    private Integer commitCount;

    /**
     * 部署次数
     */
    @Schema(description = "部署次数")
    private Integer deployCount;

    /**
     * 构造函数
     */
    public AppStatistics() {
        this.projectCount = 0;
        this.commitCount = 0;
        this.deployCount = 0;
    }

    /**
     * 构造函数
     * 
     * @param projectCount 项目数量
     * @param commitCount 代码提交次数
     * @param deployCount 部署次数
     */
    public AppStatistics(Integer projectCount, Integer commitCount, Integer deployCount) {
        this.projectCount = projectCount != null ? projectCount : 0;
        this.commitCount = commitCount != null ? commitCount : 0;
        this.deployCount = deployCount != null ? deployCount : 0;
    }
}