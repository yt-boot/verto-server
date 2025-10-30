package com.verto.modules.appmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

import com.verto.common.jackson.JsonToStringDeserializer;

/**
 * 应用配置管理实体
 */
@Data
@TableName("app_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "应用配置管理")
public class AppConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /** 配置名称 */
    @Schema(description = "配置名称")
    private String name;

    /** 配置类型：pipeline / tracking / code_review */
    @Schema(description = "配置类型：pipeline / tracking / code_review")
    private String type;

    /** 状态：enabled / disabled */
    @Schema(description = "状态：enabled / disabled")
    private String status;

    /** 环境：dev / test / prod 等 */
    @Schema(description = "环境：dev / test / prod 等")
    private String environment;

    /** 描述 */
    @Schema(description = "描述")
    private String description;

    /** 关联应用ID */
    @Schema(description = "关联应用ID")
    private String appId;

    /** 配置内容（JSON字符串） */
    @Schema(description = "配置内容（JSON字符串）")
    @JsonDeserialize(using = JsonToStringDeserializer.class)
    private String config;

    /** 创建人 */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}