package org.jeecg.modules.verto.staff.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 人员管理实体类（对应表：verto_staff）
 */
@Data
@TableName("verto_staff")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "人员管理")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 姓名 */
    @Excel(name = "姓名", width = 15)
    private String name;

    /** 工号 */
    @Excel(name = "工号", width = 15)
    private String employeeNo;

    /** 邮箱 */
    @Excel(name = "邮箱", width = 25)
    private String email;

    /** 手机号 */
    @Excel(name = "手机号", width = 15)
    private String phone;

    /** 入职日期 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 15, format = "yyyy-MM-dd")
    private Date hireDate;

    /** 工作地点 */
    @Excel(name = "工作地点", width = 15)
    private String workLocation;

    /** 技能标签（JSON 字符串） */
    @Excel(name = "技能标签", width = 30)
    private String skills;

    /** 状态：1-在职，0-离职 */
    @Excel(name = "状态", width = 10)
    private Integer status;

    /** 备注 */
    @Excel(name = "备注", width = 20)
    private String remark;

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 总积分（非持久化字段，用于列表展示，如后续集成积分模块可赋值） */
    @TableField(exist = false)
    private Integer points;

    // 便于 MyBatis-Plus 构造查询条件的 getter
    public String getName() { return name; }
    public String getEmployeeNo() { return employeeNo; }
    public String getEmail() { return email; }
    public Integer getStatus() { return status; }
    public Date getHireDate() { return hireDate; }
}