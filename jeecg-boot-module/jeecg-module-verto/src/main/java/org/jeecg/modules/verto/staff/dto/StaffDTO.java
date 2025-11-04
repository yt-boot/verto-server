package org.jeecg.modules.verto.staff.dto;

import lombok.Data;

@Data
public class StaffDTO {
    private String id;
    private String name;
    private String employeeNo;
    private String email;
    private String phone;
    private String hireDate;
    private String workLocation;
    private String skills;
    private Integer status;
    private String remark;
    private String createTime;
    private String updateTime;
    private Integer points;
}