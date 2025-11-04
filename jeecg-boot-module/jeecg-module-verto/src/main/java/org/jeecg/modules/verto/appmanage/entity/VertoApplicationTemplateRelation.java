package org.jeecg.modules.verto.appmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("verto_application_template_relation")
public class VertoApplicationTemplateRelation {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String applicationId;
    private String templateId;
}