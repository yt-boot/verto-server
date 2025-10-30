package com.verto.modules.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.verto.common.api.Result;
import com.verto.modules.appmanage.entity.AppPipelineBinding;
import com.verto.modules.appmanage.service.IAppPipelineBindingService;
import com.verto.modules.pipeline.service.IJenkinsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "应用流水线绑定", description = "绑定已有 Jenkins 流水线至应用环境")
@RestController
@RequestMapping("/appmanage/pipeline/binding")
public class AppPipelineBindingController {

    @Autowired
    private IAppPipelineBindingService bindingService;

    @Autowired
    private IJenkinsService jenkinsService;

    @Operation(summary = "列表查询绑定")
    @GetMapping("/list")
    public Result<List<AppPipelineBinding>> list(@RequestParam String appId,
                                                 @RequestParam(required = false) String environment) {
        QueryWrapper<AppPipelineBinding> qw = new QueryWrapper<>();
        qw.eq("app_id", appId);
        if (StringUtils.hasText(environment)) {
            qw.eq("environment", environment);
        }
        qw.orderByDesc("status");
        qw.orderByDesc("update_time");
        List<AppPipelineBinding> list = bindingService.list(qw);
        return Result.ok(list);
    }

    @Operation(summary = "绑定详情")
    @GetMapping("/detail/{id}")
    public Result<AppPipelineBinding> detail(@PathVariable String id) {
        AppPipelineBinding b = bindingService.getById(id);
        return Result.ok(b);
    }

    @Operation(summary = "保存绑定（新增或更新）")
    @PostMapping("/save")
    public Result<String> save(@RequestBody AppPipelineBinding payload) {
        if (!StringUtils.hasText(payload.getId())) {
            payload.setCreateTime(new Date());
            payload.setCreateBy("admin");
            bindingService.save(payload);
        } else {
            payload.setUpdateTime(new Date());
            payload.setUpdateBy("admin");
            bindingService.updateById(payload);
        }
        return Result.ok("保存成功");
    }

    @Operation(summary = "删除绑定")
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable String id) {
        bindingService.removeById(id);
        return Result.ok("删除成功");
    }

    @Operation(summary = "校验 Jenkins Job 是否存在")
    @GetMapping("/validate")
    public Result<Boolean> validate(@RequestParam String jobName) {
        boolean exists = jenkinsService.jobExists(jobName);
        return Result.ok(exists);
    }
}