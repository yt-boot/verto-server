package org.jeecg.modules.verto.appmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 应用管理实体，映射表 verto_application
 * 注意：字段命名采用驼峰，与表中下划线字段自动映射（MyBatis-Plus 驼峰转换）。
 */
@Data
@TableName("verto_application")
public class VertoApplication {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 应用名称 -> app_name */
    private String appName;
    /** 应用描述 -> app_description */
    private String appDescription;
    /** Git 仓库地址 -> git_url */
    private String gitUrl;
    /** 应用领域 -> domain */
    private String domain;
    /** 状态(0/1) -> status */
    private Integer status;
    /** 额外信息(JSON) -> extra_info */
    private String extraInfo;

    /** 管理员（兼容旧字段，JSON/逗号分隔字符串）-> managers */
    private String managers;

    /** 创建人 -> create_by */
    private String createBy;

    /** 更新人 -> update_by */
    private String updateBy;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}