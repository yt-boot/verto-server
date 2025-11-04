package org.jeecg.modules.verto.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.appmanage.dto.ApplicationDTO;
import org.jeecg.modules.verto.appmanage.entity.VertoApplication;
import org.jeecg.modules.verto.appmanage.entity.VertoApplicationTemplateRelation;
import org.jeecg.modules.verto.appmanage.service.IVertoApplicationService;
import org.jeecg.modules.verto.appmanage.service.IVertoApplicationTemplateRelationService;
import org.jeecg.modules.verto.material.service.IVertoMaterialTemplateService;
import org.jeecg.modules.verto.appmanage.util.Jsons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/jeecgboot/verto/application", "/verto-backend/application"})
public class ApplicationController {

    @Autowired
    private IVertoApplicationService applicationService;
    @Autowired
    private IVertoMaterialTemplateService templateService;
    @Autowired
    private IVertoApplicationTemplateRelationService relationService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int pageNo,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String keyword) {
        QueryWrapper<VertoApplication> qw = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.lambda().like(VertoApplication::getAppName, keyword);
        }
        Page<VertoApplication> page = applicationService.page(new Page<>(pageNo, pageSize), qw);
        List<ApplicationDTO> records = page.getRecords().stream().map(this::toDTO).collect(Collectors.toList());
        return Result.OK(records);
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        VertoApplication app = new VertoApplication();
        app.setAppName((String) payload.getOrDefault("appName", ""));
        app.setAppDescription((String) payload.getOrDefault("description", ""));
        app.setGitUrl((String) payload.getOrDefault("gitUrl", ""));
        app.setStatus(parseStatus(payload.get("status")));
        app.setExtraInfo(Jsons.toJsonString(payload.get("extraInfo"))); // optional
        app.setCreateTime(new Date());
        app.setUpdateTime(new Date());
        applicationService.save(app);
        return Result.OK(toDTO(app));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable String id) {
        VertoApplication app = applicationService.getById(id);
        if (app == null) {
            return Result.error("Application not found");
        }
        return Result.OK(toDTO(app));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        VertoApplication app = applicationService.getById(id);
        if (app == null) {
            return Result.error("Application not found");
        }
        if (payload.containsKey("appName")) app.setAppName((String) payload.get("appName"));
        if (payload.containsKey("description")) app.setAppDescription((String) payload.get("description"));
        if (payload.containsKey("gitUrl")) app.setGitUrl((String) payload.get("gitUrl"));
        if (payload.containsKey("status")) app.setStatus(parseStatus(payload.get("status")));
        if (payload.containsKey("extraInfo")) app.setExtraInfo(Jsons.toJsonString(payload.get("extraInfo")));
        app.setUpdateTime(new Date());
        applicationService.updateById(app);
        return Result.OK(toDTO(app));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable String id) {
        boolean ok = applicationService.removeById(id);
        return ok ? Result.OK("Deleted") : Result.error("Delete failed");
    }

    @PostMapping("/{id}/bindTemplate")
    public Result<?> bindTemplate(@PathVariable String id, @RequestParam String templateId) {
        // enforce one-to-one binding by replacing existing relation
        relationService.remove(new QueryWrapper<VertoApplicationTemplateRelation>().lambda()
                .eq(VertoApplicationTemplateRelation::getApplicationId, id));
        VertoApplicationTemplateRelation rel = new VertoApplicationTemplateRelation();
        rel.setApplicationId(id);
        rel.setTemplateId(templateId);
        relationService.save(rel);
        return Result.OK("Bound");
    }

    @DeleteMapping("/{id}/unbindTemplate")
    public Result<?> unbindTemplate(@PathVariable String id) {
        relationService.remove(new QueryWrapper<VertoApplicationTemplateRelation>().lambda()
                .eq(VertoApplicationTemplateRelation::getApplicationId, id));
        return Result.OK("Unbound");
    }

    private ApplicationDTO toDTO(VertoApplication app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setAppName(app.getAppName());
        dto.setDescription(app.getAppDescription());
        dto.setGitUrl(app.getGitUrl());
        dto.setStatus(stringifyStatus(app.getStatus()));
        dto.setCreateTime(formatDate(app.getCreateTime()));
        dto.setUpdateTime(formatDate(app.getUpdateTime()));
        // extraInfo fields for mock alignment
        String extra = app.getExtraInfo();
        dto.setAppCode(Jsons.getString(extra, "appCode", null));
        dto.setAppType(Jsons.getString(extra, "appType", null));
        return dto;
    }

    private String formatDate(Date d) {
        if (d == null) return null;
        // Reuse consistent formatting across controllers
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }

    private Integer parseStatus(Object statusObj) {
        if (statusObj == null) return null;
        if (statusObj instanceof Number) {
            return ((Number) statusObj).intValue();
        }
        if (statusObj instanceof String) {
            String s = ((String) statusObj).trim();
            if (s.isEmpty()) return null;
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignore) {
                String lower = s.toLowerCase();
                if (lower.equals("enabled") || lower.equals("enable") || lower.equals("active") || lower.equals("true") || lower.equals("on")) {
                    return 1;
                }
                if (lower.equals("disabled") || lower.equals("disable") || lower.equals("inactive") || lower.equals("false") || lower.equals("off") || lower.equals("draft")) {
                    return 0;
                }
                // fallback
                return null;
            }
        }
        return null;
    }

    private String stringifyStatus(Integer status) {
        if (status == null) return null;
        return status == 1 ? "enabled" : "disabled";
    }
}