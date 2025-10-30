package org.jeecg.modules.verto.staff.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffPointsAggDTO {
    private String staffId;
    private Integer totalPoints;
}