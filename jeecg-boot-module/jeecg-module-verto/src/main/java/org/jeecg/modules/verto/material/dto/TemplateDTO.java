package org.jeecg.modules.verto.material.dto;

import lombok.Data;

@Data
public class TemplateDTO {
    private String id;
    private String templateName;
    private String templateCode;
    private String version;
    private String authorStaffId;
    private String description;
    private String status;
    private String createTime;
    private String updateTime;
}