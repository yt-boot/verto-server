package org.jeecg.modules.verto.staff.entity;

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

@Data
@TableName("staff_points_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "人员积分流水")
public class StaffPointsLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String staffId;

    /** 事件类型，如 'task', 'project', 'other' */
    private String eventType;

    /** 来源类型 */
    private String sourceType;

    /** 来源业务ID */
    private String sourceId;

    /** 来源名称 */
    private String sourceName;

    /** 变动积分（可正可负） */
    private Integer delta;

    private String remark;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}