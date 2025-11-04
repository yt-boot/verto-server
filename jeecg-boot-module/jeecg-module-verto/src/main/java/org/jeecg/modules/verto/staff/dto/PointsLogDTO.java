package org.jeecg.modules.verto.staff.dto;

import lombok.Data;

@Data
public class PointsLogDTO {
    private String id;
    private String staffId;
    private String staffName;
    private String eventType;
    private String sourceType;
    private String sourceId;
    private String sourceName;
    private Integer delta;
    private String remark;
    private String createTime;
}