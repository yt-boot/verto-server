package org.jeecg.modules.verto.points.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 人员实体（简化版），映射 verto_staff 表
 */
@Data
@TableName("verto_staff")
public class VertoStaff {
    @TableId
    private String id;
    private String name;
    private String employeeNo;
    private String email;
    private String phone;
    private Integer status;
}