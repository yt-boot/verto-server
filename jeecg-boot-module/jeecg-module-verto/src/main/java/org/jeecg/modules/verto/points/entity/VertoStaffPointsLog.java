package org.jeecg.modules.verto.points.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 积分流水日志实体，映射 verto_staff_points_log 表
 */
@Data
@TableName("verto_staff_points_log")
public class VertoStaffPointsLog {
    @TableId
    private String id;
    private String staffId;
    private Integer delta;
    private String eventType;
    private String sourceType;
    private String sourceId;
    private String sourceName;
    private String remark;
    private String createTime; // 简化处理为字符串
}