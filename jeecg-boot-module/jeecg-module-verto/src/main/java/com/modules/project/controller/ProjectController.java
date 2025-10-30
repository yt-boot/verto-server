package com.verto.modules.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.project.entity.Project;
import com.verto.modules.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 项目管理控制器
 * 
 * @author verto
 * @since 2024-01-27
 */
@Tag(name = "项目管理", description = "项目管理相关接口")
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    /**
     * 分页查询项目列表
     * 
     * @param project 查询条件
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询项目列表")
    @GetMapping(value = "/list")
    public Result<IPage<Project>> queryPageList(Project project,
                                                @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        
        // 主键ID精确查询
        if (project.getId() != null && !project.getId().trim().isEmpty()) {
            queryWrapper.eq("id", project.getId());
        }

        // 关联应用名称模糊查询
        if (project.getRelatedAppName() != null && !project.getRelatedAppName().trim().isEmpty()) {
            queryWrapper.like("related_app_name", project.getRelatedAppName());
        }

        // 关联应用ID精确查询
        if (project.getRelatedAppId() != null && !project.getRelatedAppId().trim().isEmpty()) {
            queryWrapper.eq("related_app_id", project.getRelatedAppId());
        }

        // 开发者姓名查询
        if (project.getDeveloperName() != null && !project.getDeveloperName().trim().isEmpty()) {
            queryWrapper.like("developer_name", project.getDeveloperName());
        }
        
        // 优先级查询
        if (project.getPriority() != null && !project.getPriority().trim().isEmpty()) {
            queryWrapper.eq("priority", project.getPriority());
        }
        
        // 状态查询
        if (project.getStatus() != null && !project.getStatus().trim().isEmpty()) {
            queryWrapper.eq("status", project.getStatus());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        Page<Project> page = new Page<>(pageNo, pageSize);
        IPage<Project> pageList = projectService.page(page, queryWrapper);
        
        return Result.ok(pageList);
    }

    /**
     * 根据ID查询项目详情
     * 
     * @param id 项目ID
     * @return 项目详情
     */
    @Operation(summary = "根据ID查询项目详情")
    @GetMapping(value = "/queryById")
    public Result<Project> queryById(@Parameter(description = "项目ID") @RequestParam(name = "id", required = true) String id) {
        Project project = projectService.getById(id);
        if (project == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(project);
    }

    /**
     * 新增项目
     * 
     * @param project 项目信息
     * @return 操作结果
     */
    @Operation(summary = "新增项目")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Project project) {
        project.setCreateTime(new Date());
        project.setCreateBy("admin"); // 实际项目中应从当前登录用户获取
        projectService.save(project);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑项目
     * 
     * @param project 项目信息
     * @return 操作结果
     */
    @Operation(summary = "编辑项目")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody Project project) {
        project.setUpdateTime(new Date());
        project.setUpdateBy("admin"); // 实际项目中应从当前登录用户获取
        projectService.updateById(project);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除项目
     * 
     * @param id 项目ID
     * @return 操作结果
     */
    @Operation(summary = "删除项目")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@Parameter(description = "项目ID") @RequestParam(name = "id", required = true) String id) {
        projectService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除项目
     * 
     * @param ids 项目ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除项目")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@Parameter(description = "项目ID列表") @RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        projectService.removeByIds(idList);
        return Result.ok("批量删除成功!");
    }

    /**
     * 根据项目ID查询关联应用
     * 
     * @param projectId 项目ID
     * @return 关联应用列表
     */
    @Operation(summary = "根据项目ID查询关联应用")
    @GetMapping(value = "/related-apps")
    public Result<List<Project>> getRelatedApps(@Parameter(description = "项目ID") @RequestParam(name = "projectId", required = true) String projectId) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", projectId);
        queryWrapper.orderByDesc("create_time");
        List<Project> projectList = projectService.list(queryWrapper);
        return Result.ok(projectList);
    }
}