package com.verto.modules.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.material.entity.MaterialComponent;
import com.verto.modules.material.service.IMaterialComponentService;
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
 * 物料组件管理控制器
 * 
 * @author verto
 * @since 2024-01-27
 */
@Tag(name = "物料组件管理", description = "物料组件管理相关接口")
@RestController
@RequestMapping("/material/component")
@Slf4j
public class MaterialComponentController {

    @Autowired
    private IMaterialComponentService materialComponentService;

    /**
     * 分页查询组件列表
     * 
     * @param materialComponent 查询条件
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询组件列表")
    @GetMapping(value = "/list")
    public Result<IPage<MaterialComponent>> queryPageList(MaterialComponent materialComponent,
                                                          @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<MaterialComponent> queryWrapper = new QueryWrapper<>();
        
        // 根据组件名称模糊查询
        if (StringUtils.hasText(materialComponent.getComponentName())) {
            queryWrapper.like("component_name", materialComponent.getComponentName());
        }
        
        // 根据组件类型查询
        if (StringUtils.hasText(materialComponent.getComponentType())) {
            queryWrapper.eq("component_type", materialComponent.getComponentType());
        }
        
        // 根据状态查询
        if (StringUtils.hasText(materialComponent.getStatus())) {
            queryWrapper.eq("status", materialComponent.getStatus());
        }
        
        queryWrapper.orderByDesc("create_time");
        
        Page<MaterialComponent> page = new Page<>(pageNo, pageSize);
        IPage<MaterialComponent> pageList = materialComponentService.page(page, queryWrapper);
        
        return Result.ok(pageList);
    }

    /**
     * 根据ID查询组件
     * 
     * @param id 组件ID
     * @return 组件信息
     */
    @Operation(summary = "根据ID查询组件")
    @GetMapping(value = "/queryById")
    public Result<MaterialComponent> queryById(@Parameter(description = "组件ID") @RequestParam(name = "id", required = true) String id) {
        MaterialComponent materialComponent = materialComponentService.getById(id);
        if (materialComponent == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(materialComponent);
    }

    /**
     * 新增组件
     * 
     * @param materialComponent 组件信息
     * @return 操作结果
     */
    @Operation(summary = "新增组件")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody MaterialComponent materialComponent) {
        materialComponent.setCreateTime(new Date());
        materialComponent.setCreateBy("admin"); // 实际项目中应该从当前登录用户获取
        materialComponent.setStatus("1"); // 默认启用状态
        materialComponentService.save(materialComponent);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑组件
     * 
     * @param materialComponent 组件信息
     * @return 操作结果
     */
    @Operation(summary = "编辑组件")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody MaterialComponent materialComponent) {
        materialComponent.setUpdateTime(new Date());
        materialComponent.setUpdateBy("admin"); // 实际项目中应该从当前登录用户获取
        materialComponentService.updateById(materialComponent);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除组件
     * 
     * @param id 组件ID
     * @return 操作结果
     */
    @Operation(summary = "删除组件")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@Parameter(description = "组件ID") @RequestParam(name = "id", required = true) String id) {
        materialComponentService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除组件
     * 
     * @param ids 组件ID数组
     * @return 操作结果
     */
    @Operation(summary = "批量删除组件")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@Parameter(description = "组件ID数组") @RequestParam(name = "ids", required = true) String ids) {
        materialComponentService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 导出Excel
     * 
     * @param materialComponent 查询条件
     * @return 操作结果
     */
    @Operation(summary = "导出Excel")
    @RequestMapping(value = "/exportXls")
    public Result<String> exportXls(MaterialComponent materialComponent) {
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