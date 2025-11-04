package org.jeecg.modules.verto.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("verto_project_pipeline")
public class VertoProjectPipeline {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String pipelineId;
    private String projectId;
    private Integer buildNumber;
    private String status; // running/success/failed/canceled
    private String gitBranch;
    private String gitCommit;
    private String config; // JSON
    private String logs; // text or JSON

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    private Long duration;
}