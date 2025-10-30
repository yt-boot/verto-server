package com.verto.modules.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.verto.common.api.Result;
import com.verto.modules.appmanage.entity.AppConfig;
import com.verto.modules.appmanage.entity.AppConfigContent;
import com.verto.modules.appmanage.entity.AppConfigRelation;
import com.verto.modules.appmanage.service.IAppConfigContentService;
import com.verto.modules.appmanage.service.IAppConfigRelationService;
import com.verto.modules.appmanage.service.IAppConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// Add missing import for Transactional annotation from Spring
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Tag(name = "配置管理", description = "配置管理相关接口")
@RestController
@RequestMapping("/appmanage/config")
@Slf4j
public class AppConfigController {

    @Autowired
    private IAppConfigService appConfigService;

    @Autowired
    private IAppConfigContentService appConfigContentService;

    @Autowired
    private IAppConfigRelationService appConfigRelationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Operation(summary = "获取配置列表")
    @GetMapping("/list")
    public Result<IPage<AppConfig>> list(
            AppConfig filter,
            @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "name", required = false) String name) {
        QueryWrapper<AppConfig> qw = new QueryWrapper<>();
        if (StringUtils.hasText(filter.getType())) {
            qw.eq("type", filter.getType());
        }
        if (StringUtils.hasText(filter.getStatus())) {
            qw.eq("status", filter.getStatus());
        }
        if (StringUtils.hasText(filter.getEnvironment())) {
            qw.eq("environment", filter.getEnvironment());
        }
        if (StringUtils.hasText(filter.getAppId())) {
            qw.eq("app_id", filter.getAppId());
        }
        if (StringUtils.hasText(name)) {
            qw.like("name", name);
        }
        qw.orderByDesc("update_time");
        Page<AppConfig> page = new Page<>(pageNo, pageSize);
        IPage<AppConfig> pageList = appConfigService.page(page, qw);
        return Result.ok(pageList);
    }

    @Operation(summary = "保存配置（新建或更新）")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> save(@RequestBody AppConfig config, HttpServletRequest request) {
        Date now = new Date();
        String userId = request.getHeader("X-User-Id");
        if (!StringUtils.hasText(config.getId())) {
            config.setCreateTime(now).setUpdateTime(now);
            if (StringUtils.hasText(userId)) {
                config.setCreateBy(userId).setUpdateBy(userId);
            }
            appConfigService.save(config);
        } else {
            config.setUpdateTime(now);
            if (StringUtils.hasText(userId)) {
                config.setUpdateBy(userId);
            }
            appConfigService.updateById(config);
        }

        // 同步保存配置详情到 app_config_content 表
        try {
            String configJson = config.getConfig();
            if (StringUtils.hasText(configJson)) {
                AppConfigContent existing = appConfigContentService.getOne(
                        new QueryWrapper<AppConfigContent>().eq("config_id", config.getId()));
                if (existing == null) {
                    AppConfigContent content = new AppConfigContent()
                            .setConfigId(config.getId())
                            .setType(config.getType())
                            .setContent(configJson)
                            .setCreateTime(now)
                            .setUpdateTime(now);
                    if (StringUtils.hasText(userId)) {
                        content.setCreateBy(userId).setUpdateBy(userId);
                    }
                    appConfigContentService.save(content);
                } else {
                    existing.setType(config.getType())
                            .setContent(configJson)
                            .setUpdateTime(now);
                    if (StringUtils.hasText(userId)) {
                        existing.setUpdateBy(userId);
                    }
                    appConfigContentService.updateById(existing);
                }

                // 重新构建关联索引
                appConfigRelationService.remove(new QueryWrapper<AppConfigRelation>().eq("config_id", config.getId()));
                try {
                    JsonNode root = objectMapper.readTree(configJson);
                    if ("pipeline".equalsIgnoreCase(config.getType())) {
                        JsonNode stages = root.path("stages");
                        if (stages.isArray()) {
                            for (JsonNode s : stages) {
                                String stageId = s.path("id").asText("");
                                String stageName = s.path("name").asText("");
                                String extra = objectMapper.writeValueAsString(s);
                                AppConfigRelation rel = new AppConfigRelation()
                                        .setConfigId(config.getId())
                                        .setRefType("pipeline_stage")
                                        .setRefId(stageId)
                                        .setRefName(stageName)
                                        .setExtra(extra)
                                        .setCreateTime(now)
                                        .setUpdateTime(now);
                                if (StringUtils.hasText(userId)) {
                                    rel.setCreateBy(userId).setUpdateBy(userId);
                                }
                                appConfigRelationService.save(rel);
                            }
                        }
                    } else if ("tracking".equalsIgnoreCase(config.getType())) {
                        JsonNode events = root.path("events");
                        if (events.isArray()) {
                            for (JsonNode e : events) {
                                String eventId = e.path("id").asText("");
                                String eventName = e.path("name").asText("");
                                String extra = objectMapper.writeValueAsString(e);
                                AppConfigRelation rel = new AppConfigRelation()
                                        .setConfigId(config.getId())
                                        .setRefType("tracking_event")
                                        .setRefId(eventId)
                                        .setRefName(eventName)
                                        .setExtra(extra)
                                        .setCreateTime(now)
                                        .setUpdateTime(now);
                                if (StringUtils.hasText(userId)) {
                                    rel.setCreateBy(userId).setUpdateBy(userId);
                                }
                                appConfigRelationService.save(rel);
                            }
                        }
                        JsonNode properties = root.path("properties");
                        if (properties.isArray()) {
                            for (JsonNode p : properties) {
                                String propKey = p.path("key").asText("");
                                String extra = objectMapper.writeValueAsString(p);
                                AppConfigRelation rel = new AppConfigRelation()
                                        .setConfigId(config.getId())
                                        .setRefType("tracking_property")
                                        .setRefId(propKey)
                                        .setRefName(propKey)
                                        .setExtra(extra)
                                        .setCreateTime(now)
                                        .setUpdateTime(now);
                                if (StringUtils.hasText(userId)) {
                                    rel.setCreateBy(userId).setUpdateBy(userId);
                                }
                                appConfigRelationService.save(rel);
                            }
                        }
                    } else if ("code_review".equalsIgnoreCase(config.getType())) {
                        JsonNode rules = root.path("rules");
                        if (rules.isArray()) {
                            for (JsonNode r : rules) {
                                String ruleId = r.path("id").asText("");
                                String ruleName = r.path("name").asText("");
                                String extra = objectMapper.writeValueAsString(r);
                                AppConfigRelation rel = new AppConfigRelation()
                                        .setConfigId(config.getId())
                                        .setRefType("code_review_rule")
                                        .setRefId(ruleId)
                                        .setRefName(ruleName)
                                        .setExtra(extra)
                                        .setCreateTime(now)
                                        .setUpdateTime(now);
                                if (StringUtils.hasText(userId)) {
                                    rel.setCreateBy(userId).setUpdateBy(userId);
                                }
                                appConfigRelationService.save(rel);
                            }
                        }
                        JsonNode reviewers = root.path("reviewers");
                        if (reviewers.isArray()) {
                            for (JsonNode rv : reviewers) {
                                String userIdField = rv.path("userId").asText("");
                                String username = rv.path("username").asText("");
                                String extra = objectMapper.writeValueAsString(rv);
                                AppConfigRelation rel = new AppConfigRelation()
                                        .setConfigId(config.getId())
                                        .setRefType("code_reviewer")
                                        .setRefId(userIdField)
                                        .setRefName(username)
                                        .setExtra(extra)
                                        .setCreateTime(now)
                                        .setUpdateTime(now);
                                if (StringUtils.hasText(userId)) {
                                    rel.setCreateBy(userId).setUpdateBy(userId);
                                }
                                appConfigRelationService.save(rel);
                            }
                        }
                    }
                } catch (Exception parseEx) {
                    log.warn("解析配置并构建索引失败: {}", parseEx.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("保存配置详情失败: {}", e.getMessage());
        }

        return Result.ok("保存成功");
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam("id") String id) {
        appConfigService.removeById(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "获取配置详情")
    @GetMapping("/detail")
    public Result<AppConfig> detail(@RequestParam("id") String id) {
        AppConfig config = appConfigService.getById(id);
        return Result.ok(config);
    }

    @Operation(summary = "复制配置")
    @PostMapping("/copy")
    public Result<AppConfig> copy(@RequestParam("id") String id, @RequestParam(name = "name", required = false) String name, HttpServletRequest request) {
        AppConfig original = appConfigService.getById(id);
        if (original == null) {
            return Result.error("配置不存在");
        }
        AppConfig clone = new AppConfig();
        clone.setName(StringUtils.hasText(name) ? name : original.getName() + " - 副本");
        clone.setType(original.getType());
        clone.setStatus("disabled");
        clone.setEnvironment(original.getEnvironment());
        clone.setDescription(original.getDescription());
        clone.setAppId(original.getAppId());
        clone.setConfig(original.getConfig());
        Date now = new Date();
        clone.setCreateTime(now).setUpdateTime(now);
        String userId = request.getHeader("X-User-Id");
        if (StringUtils.hasText(userId)) {
            clone.setCreateBy(userId).setUpdateBy(userId);
        }
        appConfigService.save(clone);
        return Result.ok(clone);
    }

    @Operation(summary = "预览配置（返回yaml/json）")
    @GetMapping("/preview")
    public Result<Map<String, Object>> preview(@RequestParam("id") String id) {
        AppConfig config = appConfigService.getById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("config", config);
        // 简单生成 yaml 预览
        String yaml = "# " + config.getName() + "\n" +
                "apiVersion: v1\n" +
                "kind: Config\n" +
                "metadata:\n  name: " + config.getName() + "\n" +
                "spec:\n  type: " + config.getType() + "\n  status: " + config.getStatus();
        payload.put("preview", Map.of("yaml", yaml, "json", prettyJson(config)));
        return Result.ok(payload);
    }

    @Operation(summary = "获取配置变更历史（示例数据）")
    @GetMapping("/history")
    public Result<IPage<Map<String, Object>>> history(@RequestParam("id") String id,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // 先返回示例数据，后续可接入真实历史表
        List<Map<String, Object>> historyList = new ArrayList<>();
        historyList.add(Map.of(
                "id", UUID.randomUUID().toString(),
                "configId", id,
                "version", "v1.2.0",
                "changeType", "update",
                "description", "更新流水线配置",
                "operator", "admin",
                "operateTime", "2024-01-25 14:20:00",
                "changes", List.of(
                        Map.of("field", "timeout", "oldValue", "300", "newValue", "600"),
                        Map.of("field", "retryCount", "oldValue", "1", "newValue", "2")
                )
        ));
        historyList.add(Map.of(
                "id", UUID.randomUUID().toString(),
                "configId", id,
                "version", "v1.1.0",
                "changeType", "create",
                "description", "创建流水线配置",
                "operator", "admin",
                "operateTime", "2024-01-15 10:30:00",
                "changes", Collections.emptyList()
        ));
        // 简单分页
        int total = historyList.size();
        Page<Map<String, Object>> page = new Page<>(pageNo, pageSize);
        page.setTotal(total);
        page.setRecords(historyList);
        return Result.ok(page);
    }

    @Operation(summary = "验证配置")
    @PostMapping("/validate")
    public Result<Map<String, Object>> validate(@RequestParam("type") String type, @RequestBody(required = false) Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        List<Map<String, String>> errors = new ArrayList<>();
        Map<String, String> warn = Map.of("field", "general", "message", "请检查配置项");
        res.put("warnings", new ArrayList<>(List.of(warn)));
        if (body == null) {
            res.put("valid", false);
            errors.add(Map.of("field", "config", "message", "配置不能为空"));
            res.put("errors", errors);
            return Result.ok(res);
        }
        Object cfgObj = body.get("config");
        if (cfgObj == null) {
            res.put("valid", false);
            errors.add(Map.of("field", "config", "message", "配置不能为空"));
            res.put("errors", errors);
            return Result.ok(res);
        }
        try {
            JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(cfgObj));
            if (node.get("name") == null || node.get("name").asText().isBlank()) {
                errors.add(Map.of("field", "name", "message", "配置名称不能为空"));
            }
            if (node.get("type") == null || node.get("type").asText().isBlank()) {
                errors.add(Map.of("field", "type", "message", "配置类型不能为空"));
            }
        } catch (Exception e) {
            errors.add(Map.of("field", "config", "message", "配置格式错误"));
        }
        boolean isValid = errors.isEmpty();
        res.put("valid", isValid);
        res.put("errors", errors);
        return Result.ok(res);
    }

    @Operation(summary = "导出配置为JSON文件")
    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody Map<String, Object> body) {
        List<String> ids = (List<String>) body.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<AppConfig> list = appConfigService.list(new QueryWrapper<AppConfig>().in("id", ids));
        String json;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (Exception e) {
            json = "[]";
        }
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = "configs_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".json";
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @Operation(summary = "导入配置（JSON文件）")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> importConfigs(@RequestPart("file") MultipartFile file,
                                                     @RequestParam(name = "appId", required = false) String appId,
                                                     HttpServletRequest request) {
        int successCount = 0;
        int errorCount = 0;
        try {
            byte[] bytes = file.getBytes();
            JsonNode root = objectMapper.readTree(bytes);
            if (root.isArray()) {
                for (JsonNode n : root) {
                    try {
                        AppConfig ac = new AppConfig();
                        ac.setName(n.path("name").asText());
                        ac.setType(n.path("type").asText());
                        ac.setStatus(n.path("status").asText("disabled"));
                        ac.setEnvironment(n.path("environment").asText());
                        ac.setDescription(n.path("description").asText());
                        ac.setAppId(StringUtils.hasText(appId) ? appId : n.path("appId").asText());
                        ac.setConfig(objectMapper.writeValueAsString(n.path("config")));
                        Date now = new Date();
                        ac.setCreateTime(now).setUpdateTime(now);
                        String userId = request.getHeader("X-User-Id");
                        if (StringUtils.hasText(userId)) {
                            ac.setCreateBy(userId).setUpdateBy(userId);
                        }
                        appConfigService.save(ac);
                        successCount++;
                    } catch (Exception ex) {
                        errorCount++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("导入配置失败: {}", e.getMessage());
            return Result.error("导入失败: " + e.getMessage());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("successCount", successCount);
        res.put("errorCount", errorCount);
        return Result.ok("导入完成，成功" + successCount + "个，失败" + errorCount + "个", res);
    }

    private String prettyJson(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Operation(summary = "执行流水线（模拟）")
    @PostMapping("/pipeline/execute")
    public Result<Map<String, Object>> executePipeline(@RequestParam("configId") String configId,
                                                       @RequestParam(name = "branch", required = false) String branch) {
        Map<String, Object> res = new HashMap<>();
        res.put("executionId", UUID.randomUUID().toString());
        res.put("status", "queued");
        res.put("branch", branch);
        res.put("configId", configId);
        return Result.ok("流水线执行已排队", res);
    }

    @Operation(summary = "停止流水线（模拟）")
    @PostMapping("/pipeline/stop")
    public Result<?> stopPipeline(@RequestParam("executionId") String executionId) {
        return Result.ok("已发送停止指令: " + executionId);
    }

    @Operation(summary = "获取流水线日志（模拟）")
    @GetMapping("/pipeline/logs")
    public Result<Map<String, Object>> getPipelineLogs(@RequestParam("executionId") String executionId,
                                                       @RequestParam(name = "stageId", required = false) String stageId) {
        Map<String, Object> res = new HashMap<>();
        res.put("executionId", executionId);
        res.put("stageId", stageId);
        res.put("logs", List.of("[INFO] Start stage " + stageId, "[INFO] Running...", "[INFO] Completed"));
        return Result.ok(res);
    }

    @Operation(summary = "获取流水线阶段列表")
    @GetMapping("/pipeline/stages")
    public Result<List<Map<String, Object>>> getPipelineStages(@RequestParam("configId") String configId) {
        AppConfig config = appConfigService.getById(configId);
        if (config == null) {
            return Result.error("配置不存在");
        }
        if (!"pipeline".equalsIgnoreCase(config.getType())) {
            return Result.error("非流水线配置");
        }
        try {
            JsonNode root = objectMapper.readTree(config.getConfig());
            JsonNode stages = root.path("stages");
            List<Map<String, Object>> list = new ArrayList<>();
            if (stages.isArray()) {
                for (JsonNode s : stages) {
                    Map<String, Object> item = objectMapper.convertValue(s, Map.class);
                    list.add(item);
                }
            }
            return Result.ok(list);
        } catch (Exception e) {
            return Result.error("解析配置失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取埋点事件列表")
    @GetMapping("/tracking/events")
    public Result<List<Map<String, Object>>> getTrackingEvents(@RequestParam("configId") String configId) {
        return extractArrayField(configId, "events");
    }

    @Operation(summary = "获取埋点属性列表")
    @GetMapping("/tracking/properties")
    public Result<List<Map<String, Object>>> getTrackingProperties(@RequestParam("configId") String configId) {
        return extractArrayField(configId, "properties");
    }

    @Operation(summary = "获取埋点统计数据（模拟）")
    @GetMapping("/tracking/statistics")
    public Result<Map<String, Object>> getTrackingStatistics(@RequestParam("configId") String configId,
                                                             @RequestParam("startDate") String startDate,
                                                             @RequestParam("endDate") String endDate) {
        Map<String, Object> res = new HashMap<>();
        res.put("configId", configId);
        res.put("startDate", startDate);
        res.put("endDate", endDate);
        res.put("summary", Map.of("page_view", 1024, "button_click", 256));
        return Result.ok(res);
    }

    @Operation(summary = "获取代码审查规则")
    @GetMapping("/code-review/rules")
    public Result<List<Map<String, Object>>> getCodeReviewRules(@RequestParam("configId") String configId) {
        return extractArrayField(configId, "rules");
    }

    @Operation(summary = "获取代码审查人员")
    @GetMapping("/code-review/reviewers")
    public Result<List<Map<String, Object>>> getCodeReviewReviewers(@RequestParam("configId") String configId) {
        return extractArrayField(configId, "reviewers");
    }

    @Operation(summary = "获取代码审查报告（模拟）")
    @GetMapping("/code-review/reports")
    public Result<Map<String, Object>> getCodeReviewReports(@RequestParam("configId") String configId,
                                                            @RequestParam("startDate") String startDate,
                                                            @RequestParam("endDate") String endDate) {
        Map<String, Object> res = new HashMap<>();
        res.put("configId", configId);
        res.put("startDate", startDate);
        res.put("endDate", endDate);
        res.put("summary", Map.of("complexity_warnings", 12, "coverage_errors", 3, "security_errors", 0));
        return Result.ok(res);
    }

    private Result<List<Map<String, Object>>> extractArrayField(String configId, String field) {
        AppConfig config = appConfigService.getById(configId);
        if (config == null) {
            return Result.error("配置不存在");
        }
        try {
            JsonNode root = objectMapper.readTree(config.getConfig());
            JsonNode arr = root.path(field);
            List<Map<String, Object>> list = new ArrayList<>();
            if (arr.isArray()) {
                for (JsonNode s : arr) {
                    Map<String, Object> item = objectMapper.convertValue(s, Map.class);
                    list.add(item);
                }
            }
            return Result.ok(list);
        } catch (Exception e) {
            return Result.error("解析配置失败: " + e.getMessage());
        }
    }
}