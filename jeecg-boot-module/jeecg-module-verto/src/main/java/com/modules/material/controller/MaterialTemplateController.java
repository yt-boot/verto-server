package com.verto.modules.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.material.entity.MaterialTemplate;
import com.verto.modules.material.service.IMaterialTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;

/**
 * 物料模板管理控制器
 * 
 * @author verto
 * @since 2024-01-27
 */
@Tag(name = "物料模板管理", description = "物料模板管理相关接口")
@RestController
@RequestMapping("/material/template")
@Slf4j
public class MaterialTemplateController {

    @Autowired
    private IMaterialTemplateService materialTemplateService;

    /**
     * 分页查询模板列表
     * 
     * @param materialTemplate 查询条件
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询模板列表")
    @GetMapping(value = "/list")
    public Result<IPage<MaterialTemplate>> queryPageList(MaterialTemplate materialTemplate,
                                                         @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<MaterialTemplate> queryWrapper = new QueryWrapper<>();
        
        // 根据模板名称模糊查询
        if (StringUtils.hasText(materialTemplate.getTemplateName())) {
            queryWrapper.like("template_name", materialTemplate.getTemplateName());
        }
        
        // 根据模板类型查询
        if (StringUtils.hasText(materialTemplate.getTemplateType())) {
            queryWrapper.eq("template_type", materialTemplate.getTemplateType());
        }
        
        // 根据状态查询
        if (StringUtils.hasText(materialTemplate.getStatus())) {
            queryWrapper.eq("status", materialTemplate.getStatus());
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<MaterialTemplate> page = new Page<>(pageNo, pageSize);
        IPage<MaterialTemplate> pageList = materialTemplateService.page(page, queryWrapper);
        
        return Result.ok(pageList);
    }

    /**
     * 根据ID查询模板
     * 
     * @param id 模板ID
     * @return 模板信息
     */
    @Operation(summary = "根据ID查询模板")
    @GetMapping(value = "/queryById")
    public Result<MaterialTemplate> queryById(@Parameter(description = "模板ID") @RequestParam(name = "id", required = true) String id) {
        MaterialTemplate materialTemplate = materialTemplateService.getById(id);
        if (materialTemplate == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(materialTemplate);
    }

    /**
     * 新增模板
     * 
     * @param materialTemplate 模板信息
     * @return 操作结果
     */
    @Operation(summary = "新增模板")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody MaterialTemplate materialTemplate) {
        log.info("接收到的模板数据: {}", materialTemplate);
        
        // 校验必填字段
        if (materialTemplate.getTemplateName() == null || materialTemplate.getTemplateName().trim().isEmpty()) {
            return Result.error("模板名称不能为空");
        }
        if (materialTemplate.getTemplateCode() == null || materialTemplate.getTemplateCode().trim().isEmpty()) {
            return Result.error("模板代码不能为空");
        }
        if (materialTemplate.getTemplateType() == null || materialTemplate.getTemplateType().trim().isEmpty()) {
            return Result.error("模板类型不能为空");
        }
        
        // 校验模板代码唯一性
        MaterialTemplate existTemplate = materialTemplateService.getOne(
            new QueryWrapper<MaterialTemplate>().eq("template_code", materialTemplate.getTemplateCode())
        );
        if (existTemplate != null) {
            return Result.error("模板代码已存在，请使用其他代码");
        }
        
        // 设置默认值
        materialTemplate.setCreateTime(new Date());
        materialTemplate.setCreateBy("admin"); // 实际项目中应该从当前登录用户获取
        if (materialTemplate.getStatus() == null) {
            materialTemplate.setStatus("1"); // 默认启用状态
        }
        
        log.info("保存前的模板数据: {}", materialTemplate);
        materialTemplateService.save(materialTemplate);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑模板
     * 
     * @param materialTemplate 模板信息
     * @return 操作结果
     */
    @Operation(summary = "编辑模板")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody MaterialTemplate materialTemplate) {
        materialTemplate.setUpdateTime(new Date());
        materialTemplate.setUpdateBy("admin"); // 实际项目中应该从当前登录用户获取
        materialTemplateService.updateById(materialTemplate);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除模板
     * 
     * @param id 模板ID
     * @return 操作结果
     */
    @Operation(summary = "删除模板")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@Parameter(description = "模板ID") @RequestParam(name = "id", required = true) String id) {
        materialTemplateService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除模板
     * 
     * @param ids 模板ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除模板")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@Parameter(description = "模板ID数组") @RequestParam(name = "ids", required = true) String ids) {
        materialTemplateService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出Excel
     * 
     * @param materialTemplate 查询条件
     * @return 操作结果
     */
    @Operation(summary = "导出Excel")
    @RequestMapping(value = "/exportXls")
    public Result<String> exportXls(MaterialTemplate materialTemplate) {
        // 实际项目中应该实现Excel导出功能
        return Result.ok("导出成功");
    }

    /**
     * 导入Excel
     * 
     * @return 操作结果
     */
    @Operation(summary = "导入Excel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<String> importExcel() {
        // 实际项目中应该实现Excel导入功能
        return Result.ok("导入成功");
    }
}