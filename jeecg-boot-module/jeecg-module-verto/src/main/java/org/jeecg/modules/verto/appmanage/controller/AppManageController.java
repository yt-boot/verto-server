package org.jeecg.modules.verto.appmanage.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.appmanage.entity.AppManage;
import org.jeecg.modules.verto.appmanage.service.IAppManageService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "应用管理")
@RestController
@RequestMapping("/appmanage/app")
@RequiredArgsConstructor
public class AppManageController {

    private final IAppManageService appManageService;

    @Operation(summary = "分页列表查询")
    @GetMapping("/list")
    public Result<IPage<AppManage>> queryPageList(AppManage app,
                                                  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<AppManage> query = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(app.getAppName())) {
            query.like(AppManage::getAppName, app.getAppName());
        }
        if (StringUtils.isNotBlank(app.getDomain())) {
            query.like(AppManage::getDomain, app.getDomain());
        }
        if (app.getStatus() != null) {
            query.eq(AppManage::getStatus, app.getStatus());
        }
        query.orderByDesc(AppManage::getCreateTime);
        IPage<AppManage> pageList = appManageService.page(new Page<>(pageNo, pageSize), query);
        return Result.OK(pageList);
    }

    @Operation(summary = "新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody AppManage app) {
        app.setCreateTime(new Date());
        app.setUpdateTime(new Date());
        boolean ok = appManageService.save(app);
        return ok ? Result.OK("添加成功！") : Result.error("添加失败");
    }

    @Operation(summary = "编辑")
    @PutMapping("/edit")
    public Result<String> edit(@RequestBody AppManage app) {
        app.setUpdateTime(new Date());
        boolean ok = appManageService.updateById(app);
        return ok ? Result.OK("编辑成功！") : Result.error("编辑失败或记录不存在");
    }

    @Operation(summary = "通过ID删除")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        boolean ok = appManageService.removeById(id);
        return ok ? Result.OK("删除成功！") : Result.error("删除失败或记录不存在");
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (StringUtils.isBlank(ids)) {
            return Result.error("参数不合法");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        appManageService.removeByIds(idList);
        return Result.OK("批量删除成功！");
    }

    @Operation(summary = "通过ID查询")
    @GetMapping("/queryById")
    public Result<AppManage> queryById(@RequestParam(name = "id") String id) {
        AppManage app = appManageService.getById(id);
        return app == null ? Result.error("未找到对应数据") : Result.OK(app);
    }
}