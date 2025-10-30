package com.verto.modules.material.entity;

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
 * 物料组件管理实体类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Data
@TableName("material_component")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "物料组件管理")
public class MaterialComponent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /**
     * 组件名称
     */
    @Schema(description = "组件名称")
    private String componentName;

    /**
     * 组件代码
     */
    @Schema(description = "组件代码")
    private String componentCode;

    /**
     * 组件类型
     */
    @Schema(description = "组件类型")
    private String componentType;

    /**
     * 分类
     */
    @Schema(description = "分类")
    private String category;

    /**
     * 组件描述
     */
    @Schema(description = "组件描述")
    private String description;

    /**
     * 组件版本
     */
    @Schema(description = "组件版本")
    private String version;

    /**
     * 作者
     */
    @Schema(description = "作者")
    private String author;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private String tags;

    /**
     * 依赖
     */
    @Schema(description = "依赖")
    private String dependencies;

    /**
     * 属性配置
     */
    @Schema(description = "属性配置")
    private String props;

    /**
     * 演示代码
     */
    @Schema(description = "演示代码")
    private String demoCode;

    /**
     * 文档说明
     */
    @Schema(description = "文档说明")
    private String documentation;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 收藏次数
     */
    @Schema(description = "收藏次数")
    private Integer starCount;

    /**
     * 状态(0-禁用,1-启用)
     */
    @Schema(description = "状态(0-禁用,1-启用)")
    private String status;

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

    // 手动添加的 setter 方法，确保编译通过
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    // 手动添加的 getter 方法，确保编译通过
    public String getComponentName() {
        return componentName;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getStatus() {
        return status;
    }
}