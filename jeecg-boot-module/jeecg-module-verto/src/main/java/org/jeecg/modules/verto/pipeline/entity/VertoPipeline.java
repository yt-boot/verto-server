package org.jeecg.modules.verto.pipeline.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 流水线定义实体，映射表 verto_pipeline
 */
@Data
@TableName("verto_pipeline")
public class VertoPipeline {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 应用ID -> application_id */
    private String applicationId;
    
    /** 流水线名称 -> pipeline_name */
    private String pipelineName;
    
    /** 流水线类型 -> pipeline_type (build:构建,deploy:部署,test:测试) */
    private String pipelineType;
    
    /** 状态 -> status (enabled/disabled) */
    private String status;
    
    /** 流水线URL -> job_url */
    private String jobUrl;
    
    /** 备注描述 -> description */
    private String description;
    
    /** 流水线配置 -> config (JSON) */
    private String config;

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
}