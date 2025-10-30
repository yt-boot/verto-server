package com.verto.modules.appmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 应用管理实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
@TableName("app_manage")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "应用管理")
public class AppManage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String appDescription;

    /**
     * Git仓库地址
     */
    @Schema(description = "Git仓库地址")
    private String gitUrl;

    /**
     * 应用领域
     */
    @Schema(description = "应用领域")
    private String domain;

    /**
     * 管理员列表(JSON数组)
     */
    @Schema(description = "管理员列表")
    private String managers;

    /**
     * 状态(0:禁用,1:启用)
     */
    @Schema(description = "状态(0:禁用,1:启用)")
    private Integer status;

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
     * 设置创建时间
     */
    public AppManage setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    /**
     * 设置创建人
     */
    public AppManage setCreateBy(String createBy) {
        this.createBy = createBy;
        return this;
    }

    /**
     * 设置更新时间
     */
    public AppManage setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    /**
     * 设置更新人
     */
    public AppManage setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
        return this;
    }

    /**
     * 获取应用领域
     */
    public String getDomain() {
        return domain;
    }

    /**
     * 获取状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 获取应用名称
     */
    public String getAppName() {
        return appName;
    }
}