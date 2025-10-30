package com.verto.modules.staff.entity;

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
 * 人员管理实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
@TableName("staff")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "人员管理")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String name;

    /**
     * 工号
     */
    @Schema(description = "工号")
    private String employeeNo;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 入职日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "入职日期")
    private Date hireDate;

    /**
     * 工作地点
     */
    @Schema(description = "工作地点")
    private String workLocation;

    /**
     * 技能标签
     */
    @Schema(description = "技能标签")
    private String skills;

    /**
     * 状态：1-在职，0-离职
     */
    @Schema(description = "状态：1-在职，0-离职")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 总积分（非持久化字段，用于列表展示）
     */
    @TableField(exist = false)
    @Schema(description = "总积分")
    private Integer points;

    // 手动添加必要的 getter 和 setter 方法，确保编译通过
    public String getName() {
        return name;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getEmail() {
        return email;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public String getSkills() {
        return skills;
    }

    public Integer getStatus() {
        return status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}