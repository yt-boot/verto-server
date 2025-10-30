package com.verto.modules.staff.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.staff.entity.Staff;
import com.verto.modules.staff.service.IStaffService;
import com.verto.modules.staff.service.IStaffPointsLogService;
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
 * 人员管理控制器
 * 
 * @author verto
 * @since 2024-01-27
 */
@Tag(name = "人员管理", description = "人员管理相关接口")
@RestController
@RequestMapping("/staff")
@Slf4j
public class StaffController {

    @Autowired
    private IStaffService staffService;

    @Autowired
    private IStaffPointsLogService staffPointsLogService;

    /**
     * 分页查询人员列表
     * 
     * @param staff 查询条件
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询人员列表")
    @GetMapping(value = "/list")
    public Result<IPage<Staff>> queryPageList(Staff staff,
                                              @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
        
        // 姓名模糊查询
        if (staff.getName() != null && !staff.getName().trim().isEmpty()) {
            queryWrapper.like("name", staff.getName());
        }
        
        // 工号精确查询
        if (staff.getEmployeeNo() != null && !staff.getEmployeeNo().trim().isEmpty()) {
            queryWrapper.eq("employee_no", staff.getEmployeeNo());
        }
        
        // 邮箱模糊查询
        if (staff.getEmail() != null && !staff.getEmail().trim().isEmpty()) {
            queryWrapper.like("email", staff.getEmail());
        }
        
        // 工作地点查询
        if (staff.getWorkLocation() != null && !staff.getWorkLocation().trim().isEmpty()) {
            queryWrapper.eq("work_location", staff.getWorkLocation());
        }
        
        // 状态查询
        if (staff.getStatus() != null) {
            queryWrapper.eq("status", staff.getStatus());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("create_time");
        
        Page<Staff> page = new Page<>(pageNo, pageSize);
        IPage<Staff> pageList = staffService.page(page, queryWrapper);

        // 聚合每条记录的总积分并塞入非持久化字段 points
        if (pageList != null && pageList.getRecords() != null && !pageList.getRecords().isEmpty()) {
            List<Staff> records = pageList.getRecords();
            List<String> staffIds = records.stream().map(Staff::getId).toList();
            var pointsMap = staffPointsLogService.getTotalPointsMapByStaffIds(staffIds);
            records.forEach(s -> s.setPoints(pointsMap.getOrDefault(s.getId(), 0)));
        }

        return Result.ok(pageList);
    }

    /**
     * 根据ID查询人员详情
     * 
     * @param id 人员ID
     * @return 人员详情
     */
    @Operation(summary = "根据ID查询人员详情")
    @GetMapping(value = "/queryById")
    public Result<Staff> queryById(@Parameter(description = "人员ID") @RequestParam(name = "id", required = true) String id) {
        Staff staff = staffService.getById(id);
        if (staff == null) {
            return Result.error("未找到对应实体");
        }
        return Result.ok(staff);
    }

    /**
     * 新增人员
     * 
     * @param staff 人员信息
     * @return 操作结果
     */
    @Operation(summary = "新增人员")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Staff staff) {
        try {
            // 检查员工编号是否已存在
            QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("employee_no", staff.getEmployeeNo());
            Staff existingStaff = staffService.getOne(queryWrapper);
            if (existingStaff != null) {
                return Result.error("员工编号已存在，请使用其他编号！");
            }
            
            staff.setCreateTime(new Date());
            staff.setCreateBy("admin"); // 实际项目中应从当前登录用户获取
            staffService.save(staff);
            return Result.ok("添加成功！");
        } catch (org.springframework.dao.DuplicateKeyException e) {
            return Result.error("员工编号已存在，请使用其他编号！");
        } catch (Exception e) {
            log.error("新增人员失败", e);
            return Result.error("新增人员失败：" + e.getMessage());
        }
    }

    /**
     * 编辑人员
     * 
     * @param staff 人员信息
     * @return 操作结果
     */
    @Operation(summary = "编辑人员")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody Staff staff) {
        staff.setUpdateTime(new Date());
        staff.setUpdateBy("admin"); // 实际项目中应从当前登录用户获取
        staffService.updateById(staff);
        return Result.ok("编辑成功!");
    }

    /**
     * 删除人员
     * 
     * @param id 人员ID
     * @return 操作结果
     */
    @Operation(summary = "删除人员")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@Parameter(description = "人员ID") @RequestParam(name = "id", required = true) String id) {
        staffService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除人员
     * 
     * @param ids 人员ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除人员")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@Parameter(description = "人员ID列表") @RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        staffService.removeByIds(idList);
        return Result.ok("批量删除成功!");
    }

    /**
     * 获取所有在职人员列表（用于下拉选择）
     * 
     * @return 在职人员列表
     */
    @Operation(summary = "获取所有在职人员列表")
    @GetMapping(value = "/active")
    public Result<List<Staff>> getActiveStaff() {
        QueryWrapper<Staff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 在职状态
        queryWrapper.orderBy(true, true, "name");
        List<Staff> staffList = staffService.list(queryWrapper);
        return Result.ok(staffList);
    }
}