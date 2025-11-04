package org.jeecg.modules.verto.project.dto;

import lombok.Data;

@Data
public class BuildRecordDTO {
    private String id;
    private String pipelineId;
    private Integer number;
    private String status;
    private String startTime;
    private String endTime;
    private Long duration;
    private String branch;
    private String commitId;
    private String commitMessage;
    private String author;
    private String currentStage;
    private Integer progress;
}