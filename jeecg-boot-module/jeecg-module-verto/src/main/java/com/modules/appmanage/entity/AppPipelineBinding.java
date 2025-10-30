package com.verto.modules.appmanage.entity;

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
 * 应用流水线绑定实体
 * 对应表：app_pipeline_binding
 */
@Data
@TableName("app_pipeline_binding")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "应用流水线绑定")
public class AppPipelineBinding implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /** 关联应用ID */
    @Schema(description = "关联应用ID")
    private String appId;

    /** 环境：dev/test/prod 等 */
    @Schema(description = "环境：dev/test/prod 等")
    private String environment;

    /** Jenkins Job 名称 */
    @Schema(description = "Jenkins Job 名称")
    private String jobName;

    /** Jenkins Job URL（可选） */
    @Schema(description = "Jenkins Job URL（可选）")
    private String jobUrl;

    /** 状态：enabled/disabled */
    @Schema(description = "状态：enabled/disabled")
    private String status;

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