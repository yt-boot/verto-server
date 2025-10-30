package com.verto.modules.staff.entity;

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
 * 员工积分流水实体
 */
@Data
@TableName("staff_points_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "员工积分流水")
public class StaffPointsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "员工ID")
    private String staffId;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "来源类型(APP/PROJECT/COMPONENT/OTHER)")
    private String sourceType;

    @Schema(description = "来源ID")
    private String sourceId;

    @Schema(description = "来源名称")
    private String sourceName;

    @Schema(description = "积分变动(支持负数)")
    private Integer delta;

    @Schema(description = "备注")
    private String remark;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
}