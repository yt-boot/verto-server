package org.jeecg.modules.verto.pipeline.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.pipeline.entity.VertoPipeline;
import org.jeecg.modules.verto.pipeline.service.IVertoPipelineService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "应用流水线绑定")
@RestController
@RequestMapping("/verto/pipeline/binding")
@RequiredArgsConstructor
public class PipelineBindingController {

    private final IVertoPipelineService bindingService;

    @Operation(summary = "绑定列表")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam("appId") String appId,
                                            @RequestParam(value = "environment", required = false) String environment) {
        LambdaQueryWrapper<VertoPipeline> query = new LambdaQueryWrapper<>();
        query.eq(VertoPipeline::getApplicationId, appId);
        // 由于新表结构没有environment字段，此参数可能需要调整
        query.orderByDesc(VertoPipeline::getCreateTime);
        List<VertoPipeline> list = bindingService.list(query);
        Map<String, Object> payload = new HashMap<>();
        payload.put("records", list);
        payload.put("total", list.size());
        return Result.OK(payload);
    }

    @Operation(summary = "保存绑定（新增或编辑）")
    @PostMapping("/save")
    public Result<String> save(@RequestBody VertoPipeline binding) {
        Date now = new Date();
        if (StringUtils.isBlank(binding.getStatus())) {
            binding.setStatus("enabled");
        }
        // 确保jobUrl字段可以正确处理，允许为空
        if (StringUtils.isBlank(binding.getId())) {
            // 新增
            binding.setCreateTime(now);
            binding.setUpdateTime(now);
            boolean ok = bindingService.save(binding);
            return ok ? Result.OK("保存成功！") : Result.error("保存失败");
        } else {
            // 编辑
            binding.setUpdateTime(now);
            boolean ok = bindingService.updateById(binding);
            return ok ? Result.OK("编辑成功！") : Result.error("编辑失败或记录不存在");
        }
    }

    @Operation(summary = "删除绑定")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam("id") String id) {
        boolean ok = bindingService.removeById(id);
        return ok ? Result.OK("删除成功！") : Result.error("删除失败或记录不存在");
    }

    @Operation(summary = "绑定详情")
    @GetMapping("/detail")
    public Result<VertoPipeline> detail(@RequestParam("id") String id) {
        VertoPipeline binding = bindingService.getById(id);
        return binding == null ? Result.error("未找到对应数据") : Result.OK(binding);
    }

    @Operation(summary = "校验流水线名称是否可用")
    @PostMapping("/validate")
    public Result<Map<String, Object>> validate(@RequestBody(required = false) Map<String, String> body,
                                                @RequestParam(value = "pipelineName", required = false) String pipelineName) {
        String name = pipelineName;
        if (StringUtils.isBlank(name) && body != null) {
            name = body.get("pipelineName");
        }
        if (StringUtils.isBlank(name)) {
            return Result.error("参数不合法：pipelineName 不能为空");
        }
        LambdaQueryWrapper<VertoPipeline> query = new LambdaQueryWrapper<>();
        query.eq(VertoPipeline::getPipelineName, name);
        boolean exists = bindingService.count(query) > 0;
        Map<String, Object> payload = new HashMap<>();
        payload.put("valid", !exists);
        payload.put("message", exists ? "流水线名称已存在" : "可用");
        return Result.OK(payload);
    }
}