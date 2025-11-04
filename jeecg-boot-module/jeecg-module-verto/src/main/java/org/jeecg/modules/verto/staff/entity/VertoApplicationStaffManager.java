package org.jeecg.modules.verto.staff.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("verto_application_staff_manager")
public class VertoApplicationStaffManager {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String applicationId;
    private String staffId;
}