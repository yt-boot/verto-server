package org.jeecg.modules.verto.project.dto;

import lombok.Data;

@Data
public class PipelineDTO {
    private String id;
    private String applicationId;
    private String name;
    private String description;
    private boolean enabled;
    private boolean autoTrigger;
    private String environments; // JSON string of environments
    private String createTime;
    private String updateTime;
}