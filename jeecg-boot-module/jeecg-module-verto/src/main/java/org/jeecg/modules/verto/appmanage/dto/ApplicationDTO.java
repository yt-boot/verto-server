package org.jeecg.modules.verto.appmanage.dto;

import lombok.Data;

@Data
public class ApplicationDTO {
    private String id;
    private String appName;
    private String appCode;
    private String appType;
    private String status; // PRODUCTION/DEVELOPMENT/DISABLED etc
    private String description;
    private String gitUrl;
    private String createTime;
    private String updateTime;
}