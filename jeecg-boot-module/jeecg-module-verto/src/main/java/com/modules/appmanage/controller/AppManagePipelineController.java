package com.verto.modules.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.pipeline.entity.ProjectPipeline;
import com.verto.modules.pipeline.service.IProjectPipelineService;
import com.verto.modules.project.entity.Project;
import com.verto.modules.project.service.IProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 应用维度的流水线接口
 * - 返回应用下的流水线配置集合
 * - 聚合应用下各项目与分支的流水线运行历史
 */
@Tag(name = "应用流水线", description = "应用维度流水线相关接口")
@RestController
@RequestMapping("/appmanage/pipeline")
@Slf4j
public class AppManagePipelineController {

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IProjectPipelineService projectPipelineService;

    /**
     * 返回应用的流水线配置集合（占位实现）
     * 说明：当前项目尚未落库应用级流水线配置，先返回示例集合，后续可接入真实配置表。
     */
    @Operation(summary = "获取应用的流水线配置集合")
    @GetMapping("/config")
    public Result<List<Map<String, Object>>> getAppPipelineConfigs(
            @Parameter(description = "应用ID") @RequestParam(name = "appId", required = true) String appId) {

        // TODO: 若后续增加应用级配置表（例如 app_pipeline_config），此处改为查询数据库并返回集合
        List<Map<String, Object>> configs = new ArrayList<>();

        Map<String, Object> testCfg = new HashMap<>();
        testCfg.put("id", UUID.randomUUID().toString());
        testCfg.put("name", "测试流水线");
        testCfg.put("env", "test");
        testCfg.put("enabled", true);
        testCfg.put("updatedAt", new Date());
        testCfg.put("jenkinsJob", "app-" + appId + "-test");
        testCfg.put("pipelineYaml", "stages:\n  - build\n  - test\n");

        Map<String, Object> prodCfg = new HashMap<>();
        prodCfg.put("id", UUID.randomUUID().toString());
        prodCfg.put("name", "生产流水线");
        prodCfg.put("env", "prod");
        prodCfg.put("enabled", true);
        prodCfg.put("updatedAt", new Date());
        prodCfg.put("jenkinsJob", "app-" + appId + "-prod");
        prodCfg.put("pipelineYaml", "stages:\n  - build\n  - deploy\n");

        configs.add(testCfg);
        configs.add(prodCfg);

        return Result.ok(configs);
    }

    /**
     * 按应用聚合流水线运行历史
     * 支持按分支/提交号搜索、状态筛选、时间范围与分页。
     * 返回记录包含：projectId、branch、buildNumber、status、commitId、author(触发人)、开始/结束时间等。
     */
    @Operation(summary = "获取应用维度的流水线运行历史")
    @GetMapping("/history")
    public Result<IPage<ProjectPipeline>> getAppPipelineHistory(
            @Parameter(description = "应用ID") @RequestParam(name = "appId", required = true) String appId,
            @Parameter(description = "搜索关键字（匹配分支名/提交号）") @RequestParam(name = "search", required = false) String search,
            @Parameter(description = "状态") @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "起始日期（yyyy-MM-dd或yyyy-MM-dd HH:mm:ss）") @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd或yyyy-MM-dd HH:mm:ss）") @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "页码") @RequestParam(name = "page", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        // 1) 查找应用下的项目（按 related_app_id 关联）
        QueryWrapper<Project> pjWrapper = new QueryWrapper<>();
        // 仅选择主键ID，避免因历史字段（如 priority）未建表导致的 SQL 错误
        pjWrapper.select("id");
        pjWrapper.eq("related_app_id", appId);
        List<Project> projects = projectService.list(pjWrapper);

        if (projects == null || projects.isEmpty()) {
            Page<ProjectPipeline> emptyPage = new Page<>(pageNo, pageSize);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);
            return Result.ok(emptyPage);
        }

        // 2) 构造可用的 project_id 集合（使用项目主键ID）
        Set<String> projectIds = new HashSet<>();
        for (Project p : projects) {
            if (p.getId() != null) {
                projectIds.add(p.getId());
            }
        }

        // 3) 查询流水线历史
        QueryWrapper<ProjectPipeline> ppWrapper = new QueryWrapper<>();
        if (!projectIds.isEmpty()) {
            ppWrapper.in("project_id", projectIds);
        }

        // 搜索匹配分支名或提交号
        if (StringUtils.hasText(search)) {
            ppWrapper.and(w -> w.like("git_branch", search).or().like("git_commit", search));
        }

        // 状态筛选
        if (StringUtils.hasText(status)) {
            ppWrapper.eq("status", status);
        }

        // 时间范围（按 start_time）
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);
        if (start != null && end != null) {
            ppWrapper.between("start_time", start, end);
        } else if (start != null) {
            ppWrapper.ge("start_time", start);
        } else if (end != null) {
            ppWrapper.le("start_time", end);
        }

        ppWrapper.orderByDesc("create_time");

        Page<ProjectPipeline> page = new Page<>(pageNo, pageSize);
        IPage<ProjectPipeline> pageList = projectPipelineService.page(page, ppWrapper);

        return Result.ok(pageList);
    }

    private Date parseDate(String s) {
        if (!StringUtils.hasText(s)) return null;
        List<String> patterns = Arrays.asList("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
        for (String p : patterns) {
            try {
                return new SimpleDateFormat(p).parse(s);
            } catch (ParseException ignored) {}
        }
        return null;
    }
}