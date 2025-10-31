package org.jeecg.modules.verto.material.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.material.entity.MaterialTemplate;
import org.jeecg.modules.verto.material.service.IMaterialTemplateService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "物料模板管理")
@RestController
@RequestMapping("/material/template")
@RequiredArgsConstructor
public class MaterialTemplateController {

    private final IMaterialTemplateService materialTemplateService;

    @Operation(summary = "分页列表查询")
    @GetMapping("/list")
    public Result<IPage<MaterialTemplate>> queryPageList(
            MaterialTemplate materialTemplate,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        QueryWrapper<MaterialTemplate> query = new QueryWrapper<>();

        // 兼容前端传参：type=application 映射到 template_type
        if (StringUtils.isNotBlank(type)) {
            query.eq("template_type", type);
        }

        if (StringUtils.isNotBlank(materialTemplate.getTemplateName())) {
            query.like("template_name", materialTemplate.getTemplateName());
        }
        if (StringUtils.isNotBlank(materialTemplate.getTemplateType())) {
            query.eq("template_type", materialTemplate.getTemplateType());
        }
        if (StringUtils.isNotBlank(materialTemplate.getStatus())) {
            query.eq("status", materialTemplate.getStatus());
        }
        query.orderByDesc("create_time");

        IPage<MaterialTemplate> pageList = materialTemplateService.page(new Page<>(pageNo, pageSize), query);
        return Result.OK(pageList);
    }
}