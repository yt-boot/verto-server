package com.verto.modules.staff.entity.dto;

import lombok.Data;

/**
 * 员工积分聚合结果 DTO
 */
@Data
public class StaffPointsAggDTO {
    private String staffId;
    private Integer totalPoints;
}