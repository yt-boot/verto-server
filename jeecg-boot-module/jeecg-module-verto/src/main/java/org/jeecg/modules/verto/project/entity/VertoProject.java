package org.jeecg.modules.verto.project.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("verto_project")
public class VertoProject {
    @TableId
    private String id;
    private String projectName;
}