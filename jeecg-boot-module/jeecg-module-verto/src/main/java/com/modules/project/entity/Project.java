package com.verto.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 项目管理实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
@TableName("project")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "项目管理")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 项目类型(requirement:需求,bug:缺陷)
     */
    @Schema(description = "项目类型(requirement:需求,bug:缺陷)")
    private String projectType;

    /**
     * 需求ID
     */
    @Schema(description = "需求ID")
    private String requirementId;

    /**
     * 缺陷ID
     */
    @Schema(description = "缺陷ID")
    private String bugId;

    /**
     * 项目标题
     */
    @Schema(description = "项目标题")
    private String title;

    /**
     * 项目描述
     */
    @Schema(description = "项目描述")
    private String description;

    /**
     * 关联应用ID
     */
    @Schema(description = "关联应用ID")
    private String relatedAppId;

    /**
     * 关联应用名称
     */
    @Schema(description = "关联应用名称")
    private String relatedAppName;

    /**
     * 开发者ID
     */
    @Schema(description = "开发者ID")
    private String developerId;

    /**
     * 开发者姓名
     */
    @Schema(description = "开发者姓名")
    private String developerName;

    /**
     * 优先级(低:low, 中:medium, 高:high)
     */
    @Schema(description = "优先级(低:low, 中:medium, 高:high)")
    private String priority;

    /**
     * 设计链接(JSON数组)
     */
    @Schema(description = "设计链接(JSON数组)")
    private String designLinks;

    /**
     * 开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 测试时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "测试时间")
    private Date testTime;

    /**
     * 上线时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上线时间")
    private Date onlineTime;

    /**
     * 发布时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "发布时间")
    private Date releaseTime;

    /**
     * 状态(planning, developing, testing, released)
     */
    @Schema(description = "状态(planning, developing, testing, released)")
    private String status;

    /**
     * Git分支
     */
    @Schema(description = "Git分支")
    private String gitBranch;

    /**
     * 应用配置(JSON)
     */
    @Schema(description = "应用配置(JSON)")
    private String appConfig;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}