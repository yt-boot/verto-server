package org.jeecg.modules.verto.points.dto;

import lombok.Data;

/**
 * 积分调整请求体
 */
@Data
public class PointsAdjustDTO {
    private String staffId;   // 必填
    private Integer delta;    // 必填，支持负数和正数
    private String remark;    // 备注，可选
    private String sourceType; // 来源类型，可选
    private String sourceId;   // 来源ID，可选
    private String eventType;  // 事件类型，可选
}