package com.verto.modules.pipeline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目流水线实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
@TableName("project_pipeline")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目流水线")
public class ProjectPipeline implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    @TableField("project_id")
    private String projectId;

    /**
     * 构建编号
     */
    @Schema(description = "构建编号")
    @TableField("build_number")
    private Integer buildNumber;

    /**
     * 构建状态：success-成功，failed-失败，running-运行中，pending-等待中，cancelled-已取消
     */
    @Schema(description = "构建状态：success-成功，failed-失败，running-运行中，pending-等待中，cancelled-已取消")
    private String status;

    /**
     * 分支名称
     */
    @Schema(description = "分支名称")
    @TableField("git_branch")
    private String branch;

    /**
     * 提交ID
     */
    @Schema(description = "提交ID")
    @TableField("git_commit")
    private String commitId;

    /**
     * 提交信息
     */
    @Schema(description = "提交信息")
    @TableField(exist = false)
    private String commitMessage;

    /**
     * 提交作者
     */
    @Schema(description = "提交作者")
    @TableField(exist = false)
    private String author;

    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    @TableField("end_time")
    private Date endTime;

    /**
     * 持续时间（秒）
     */
    @Schema(description = "持续时间（秒）")
    private Integer duration;

    /**
     * 当前阶段
     */
    @Schema(description = "当前阶段")
    @TableField(exist = false)
    private String currentStage;

    /**
     * 进度百分比
     */
    @Schema(description = "进度百分比")
    @TableField(exist = false)
    private Integer progress;

    /**
     * 构建日志
     */
    @Schema(description = "构建日志")
    @TableField(exist = false)
    private String buildLogs;

    /**
     * 测试日志
     */
    @Schema(description = "测试日志")
    @TableField(exist = false)
    private String testLogs;

    /**
     * 部署日志
     */
    @Schema(description = "部署日志")
    @TableField(exist = false)
    private String deployLogs;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField("update_time")
    private Date updateTime;

    // 手动添加必要的 setter 方法，确保编译通过
    public void setStatus(String status) {
        this.status = status;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDeployLogs() {
        return deployLogs;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getBuildLogs() {
        return buildLogs;
    }

    public String getTestLogs() {
        return testLogs;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getId() {
        return id;
    }
}