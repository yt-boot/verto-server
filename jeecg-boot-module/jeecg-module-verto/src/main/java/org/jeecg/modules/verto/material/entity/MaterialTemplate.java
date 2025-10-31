package org.jeecg.modules.verto.material.entity;

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
 * 物料模板管理实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("material_template")
@Schema(description = "物料模板管理")
public class MaterialTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private String id;

    /** 模板名称 */
    @TableField("template_name")
    @Schema(description = "模板名称")
    private String templateName;

    /** 模板代码 */
    @TableField("template_code")
    @Schema(description = "模板代码")
    private String templateCode;

    /** 模板类型 */
    @TableField("template_type")
    @Schema(description = "模板类型")
    private String templateType;

    /** 分类 */
    @TableField("category")
    @Schema(description = "分类")
    private String category;

    /** 模板描述 */
    @TableField("description")
    @Schema(description = "模板描述")
    private String description;

    /** 模板版本 */
    @TableField("version")
    @Schema(description = "模板版本")
    private String version;

    /** 作者 */
    @TableField("author")
    @Schema(description = "作者")
    private String author;

    /** 标签 */
    @TableField("tags")
    @Schema(description = "标签")
    private String tags;

    /** 框架 */
    @TableField("framework")
    @Schema(description = "框架")
    private String framework;

    /** 预览图片 */
    @TableField("preview_image")
    @Schema(description = "预览图片")
    private String previewImage;

    /** 源代码 */
    @TableField("source_code")
    @Schema(description = "源代码")
    private String sourceCode;

    /** 配置架构 */
    @TableField("config_schema")
    @Schema(description = "配置架构")
    private String configSchema;

    /** 演示地址 */
    @TableField("demo_url")
    @Schema(description = "演示地址")
    private String demoUrl;

    /** 文档说明 */
    @TableField("documentation")
    @Schema(description = "文档说明")
    private String documentation;

    /** 下载次数 */
    @TableField("download_count")
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /** 收藏次数 */
    @TableField("star_count")
    @Schema(description = "收藏次数")
    private Integer starCount;

    /** 状态(0-禁用,1-启用) */
    @TableField("status")
    @Schema(description = "状态(0-禁用,1-启用)")
    private String status;

    /** 创建人 */
    @TableField("create_by")
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    /** 更新人 */
    @TableField("update_by")
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
}