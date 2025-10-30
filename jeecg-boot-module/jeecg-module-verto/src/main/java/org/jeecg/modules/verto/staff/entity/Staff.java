package org.jeecg.modules.verto.staff.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 人员管理实体类（对接表：staff）
 */
@Data
@TableName("staff")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "人员管理")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 姓名 */
    private String name;

    /** 工号 */
    private String employeeNo;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 入职日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date hireDate;

    /** 工作地点 */
    private String workLocation;

    /** 技能标签（JSON 字符串） */
    private String skills;

    /** 状态：1-在职，0-离职 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 总积分（非持久化字段，用于列表展示） */
    @TableField(exist = false)
    private Integer points;

    // 以下 getter/setter 便于 MyBatis-Plus 处理；Lombok 已包含，但保留关键字段 getter 以防反射序列化依赖
    public String getName() { return name; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmail() { return email; }
    public String getWorkLocation() { return workLocation; }
    public Date getHireDate() { return hireDate; }
    public String getSkills() { return skills; }
    public Integer getStatus() { return status; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}