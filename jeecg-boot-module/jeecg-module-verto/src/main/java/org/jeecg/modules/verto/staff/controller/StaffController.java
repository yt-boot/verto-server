package org.jeecg.modules.verto.staff.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.staff.entity.Staff;
import org.jeecg.modules.verto.staff.service.IStaffPointsLogService;
import org.jeecg.modules.verto.staff.service.IStaffService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "人员管理")
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final IStaffService staffService;
    private final IStaffPointsLogService staffPointsLogService;

    @Operation(summary = "分页列表查询")
    @GetMapping("/list")
    public Result<IPage<Staff>> queryPageList(Staff staff,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Staff> query = new LambdaQueryWrapper<Staff>();
        if (StringUtils.isNotBlank(staff.getName())) {
            query.like(Staff::getName, staff.getName());
        }
        if (StringUtils.isNotBlank(staff.getEmployeeNo())) {
            query.eq(Staff::getEmployeeNo, staff.getEmployeeNo());
        }
        if (StringUtils.isNotBlank(staff.getEmail())) {
            query.eq(Staff::getEmail, staff.getEmail());
        }
        if (staff.getStatus() != null) {
            query.eq(Staff::getStatus, staff.getStatus());
        }
        query.orderByDesc(Staff::getCreateTime);
        Page<Staff> page = new Page<>(pageNo, pageSize);
        IPage<Staff> pageList = staffService.page(page, query);

        // 聚合积分
        List<String> staffIds = new ArrayList<>();
        for (Staff s : pageList.getRecords()) {
            staffIds.add(s.getId());
        }
        Map<String, Integer> pointsMap = staffPointsLogService.getTotalPointsByStaffIds(staffIds);
        for (Staff s : pageList.getRecords()) {
            s.setPoints(pointsMap.getOrDefault(s.getId(), 0));
        }

        return Result.OK(pageList);
    }

    @Operation(summary = "新增")
    @PostMapping("/add")
    public Result<String> add(@RequestBody Staff staff) {
        staff.setCreateTime(new Date());
        staff.setUpdateTime(new Date());
        boolean ok = staffService.save(staff);
        return ok ? Result.OK("添加成功！") : Result.error("添加失败");
    }

    @Operation(summary = "编辑")
    @PutMapping("/edit")
    public Result<String> edit(@RequestBody Staff staff) {
        staff.setUpdateTime(new Date());
        boolean ok = staffService.updateById(staff);
        return ok ? Result.OK("编辑成功！") : Result.error("编辑失败或记录不存在");
    }

    @Operation(summary = "通过ID删除")
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        boolean ok = staffService.removeById(id);
        return ok ? Result.OK("删除成功！") : Result.error("删除失败或记录不存在");
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (StringUtils.isBlank(ids)) {
            return Result.error("参数不合法");
        }
        List<String> idList = Arrays.asList(ids.split(","));
        staffService.removeByIds(idList);
        return Result.OK("批量删除成功！");
    }

    @Operation(summary = "通过ID查询")
    @GetMapping("/queryById")
    public Result<Staff> queryById(@RequestParam(name = "id") String id) {
        Staff staff = staffService.getById(id);
        if (staff == null) {
            return Result.error("未找到对应数据");
        }
        Integer points = staffPointsLogService.getTotalPointsByStaffId(id);
        staff.setPoints(points);
        return Result.OK(staff);
    }

    @Operation(summary = "获取在职人员列表")
    @GetMapping("/active")
    public Result<List<Staff>> active() {
        List<Staff> list = staffService.list(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getStatus, 1)
                .orderByAsc(Staff::getName));
        return Result.OK(list);
    }
}