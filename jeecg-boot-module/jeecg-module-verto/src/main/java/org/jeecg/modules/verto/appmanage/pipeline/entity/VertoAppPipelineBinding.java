package org.jeecg.modules.verto.appmanage.pipeline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 流水线绑定实体，映射表 app_pipeline_binding
 */
@Data
@TableName("app_pipeline_binding")
public class VertoAppPipelineBinding {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 应用ID -> app_id */
    private String appId;
    /** 运行环境 -> environment */
    private String environment;
    /** 任务名称 -> job_name */
    private String jobName;
    /** 任务链接 -> job_url */
    private String jobUrl;
    /** 状态 -> status (enabled/disabled) */
    private String status;

    /** 创建人 -> create_by */
    private String createBy;
    /** 创建时间 -> create_time */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 -> update_by */
    private String updateBy;
    /** 更新时间 -> update_time */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注字段（仅前端展示使用，不入库）
     */
    @TableField(exist = false)
    private String remark;
}