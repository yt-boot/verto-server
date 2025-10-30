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
 * 配置与子项的关联索引表
 * 按类型拆分：流水线阶段、埋点事件/属性、代码审查规则/评审人
 */
@Data
@TableName("app_config_relation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "配置关联索引表")
public class AppConfigRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "关联配置ID")
    private String configId;

    /**
     * 关联类型：pipeline_stage / tracking_event / tracking_property / code_review_rule / code_reviewer
     */
    @Schema(description = "关联类型")
    private String refType;

    @Schema(description = "子项ID")
    private String refId;

    @Schema(description = "子项名称")
    private String refName;

    /**
     * 额外信息（JSON字符串），用于保存该子项的详细属性
     */
    @Schema(description = "额外信息（JSON字符串）")
    private String extra;

    @Schema(description = "创建人")
    private String createBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}