package org.jeecg.modules.verto.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 项目管理实体，映射表 verto_project
 * 注意：字段命名采用驼峰，与表中下划线字段自动映射（MyBatis-Plus 驼峰转换）。
 */
@Data
@TableName("verto_project")
public class VertoProject {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 项目类型(requirement/bug) -> project_type */
    private String projectType;
    /** 需求ID -> requirement_id */
    private String requirementId;
    /** 缺陷ID -> bug_id */
    private String bugId;
    /** 项目标题 -> title */
    private String title;
    /** 项目描述 -> description */
    private String description;
    /** 关联应用ID -> related_app_id */
    private String relatedAppId;
    /** 关联应用名称 -> related_app_name */
    private String relatedAppName;
    /** 开发人员ID -> developer_id */
    private String developerId;
    /** 开发人员名称 -> developer_name */
    private String developerName;
    /** 设计链接(JSON字符串) -> design_links */
    private String designLinks;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date testTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date onlineTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date releaseTime;

    /** 状态 -> status */
    private String status;
    /** Git分支 -> git_branch */
    private String gitBranch;
    /** 应用配置(JSON字符串) -> app_config */
    private String appConfig;

    /** 创建人 -> create_by */
    private String createBy;
    /** 更新人 -> update_by */
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 优先级 -> priority */
    private String priority;
}