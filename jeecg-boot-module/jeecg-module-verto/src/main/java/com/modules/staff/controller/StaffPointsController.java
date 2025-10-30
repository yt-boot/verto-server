package com.verto.modules.staff.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.verto.common.api.Result;
import com.verto.modules.staff.entity.Staff;
import com.verto.modules.staff.entity.StaffPointsLog;
import com.verto.modules.staff.service.IStaffPointsLogService;
import com.verto.modules.staff.service.IStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Tag(name = "人员积分", description = "人员积分相关接口")
@RestController
@RequestMapping("/staff/points")
public class StaffPointsController {

    @Autowired
    private IStaffPointsLogService staffPointsLogService;

    @Autowired
    private IStaffService staffService;

    /**
     * 员工总积分摘要
     */
    @Operation(summary = "员工总积分摘要")
    @GetMapping("/summary")
    public Result<Integer> summary(@Parameter(description = "员工ID") @RequestParam(name = "staffId") String staffId) {
        Integer total = staffPointsLogService.getTotalPointsByStaffId(staffId);
        return Result.ok(total);
    }

    /**
     * 员工积分日志分页（按员工）
     */
    @Operation(summary = "员工积分日志分页（按员工）")
    @GetMapping("/logs")
    public Result<IPage<StaffPointsLog>> logs(
            @Parameter(description = "员工ID") @RequestParam(name = "staffId") String staffId,
            @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "事件类型") @RequestParam(name = "eventType", required = false) String eventType,
            @Parameter(description = "来源类型") @RequestParam(name = "sourceType", required = false) String sourceType,
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(name = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(name = "endTime", required = false) String endTime,
            @Parameter(description = "关键字(备注或来源名称匹配)") @RequestParam(name = "keyword", required = false) String keyword
    ) {
        QueryWrapper<StaffPointsLog> qw = new QueryWrapper<>();
        qw.eq("staff_id", staffId);
        if (eventType != null && !eventType.isEmpty()) {
            qw.eq("event_type", eventType);
        }
        if (sourceType != null && !sourceType.isEmpty()) {
            qw.eq("source_type", sourceType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like("remark", keyword).or().like("source_name", keyword));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (startTime != null && !startTime.isEmpty()) {
                Date st = sdf.parse(startTime);
                qw.ge("create_time", st);
            }
            if (endTime != null && !endTime.isEmpty()) {
                Date et = sdf.parse(endTime);
                qw.le("create_time", et);
            }
        } catch (ParseException e) {
            // 忽略时间解析错误，返回全部
        }
        qw.orderByDesc("create_time");
        Page<StaffPointsLog> page = new Page<>(pageNo, pageSize);
        IPage<StaffPointsLog> pageList = staffPointsLogService.page(page, qw);
        return Result.ok(pageList);
    }

    /**
     * 全员积分日志分页（支持人员/时间/事件/来源筛选）
     */
    @Operation(summary = "全员积分日志分页（支持人员/时间/事件/来源筛选）")
    @GetMapping("/logs/all")
    public Result<IPage<StaffPointsLog>> logsAll(
            @Parameter(description = "员工ID(可选)") @RequestParam(name = "staffId", required = false) String staffId,
            @Parameter(description = "页码") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @Parameter(description = "每页大小") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @Parameter(description = "事件类型") @RequestParam(name = "eventType", required = false) String eventType,
            @Parameter(description = "来源类型") @RequestParam(name = "sourceType", required = false) String sourceType,
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(name = "startTime", required = false) String startTime,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(name = "endTime", required = false) String endTime,
            @Parameter(description = "关键字(备注或来源名称匹配)") @RequestParam(name = "keyword", required = false) String keyword
    ) {
        QueryWrapper<StaffPointsLog> qw = new QueryWrapper<>();
        if (staffId != null && !staffId.isEmpty()) {
            qw.eq("staff_id", staffId);
        }
        if (eventType != null && !eventType.isEmpty()) {
            qw.eq("event_type", eventType);
        }
        if (sourceType != null && !sourceType.isEmpty()) {
            qw.eq("source_type", sourceType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            qw.and(w -> w.like("remark", keyword).or().like("source_name", keyword));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (startTime != null && !startTime.isEmpty()) {
                Date st = sdf.parse(startTime);
                qw.ge("create_time", st);
            }
            if (endTime != null && !endTime.isEmpty()) {
                Date et = sdf.parse(endTime);
                qw.le("create_time", et);
            }
        } catch (ParseException e) {
            // 忽略时间解析错误，返回全部
        }
        qw.orderByDesc("create_time");
        Page<StaffPointsLog> page = new Page<>(pageNo, pageSize);
        IPage<StaffPointsLog> pageList = staffPointsLogService.page(page, qw);
        return Result.ok(pageList);
    }

    /**
     * 手动调整积分（正负皆可）—需要权限控制
     */
    @Operation(summary = "手动调整积分（需要权限）")
    @PostMapping("/adjust")
    public Result<String> adjust(@RequestBody StaffPointsLog body, @RequestHeader(value = "X-PERMISSION", required = false) String permissionCode) {
        // 简单权限控制：要求请求头包含权限码 'staff:points:adjust'（后续可接入统一权限体系）
        if (permissionCode == null || !permissionCode.contains("staff:points:adjust")) {
            return Result.error("无权限执行积分调整操作");
        }
        if (body.getStaffId() == null || body.getStaffId().trim().isEmpty()) {
            return Result.error("staffId 不能为空");
        }
        Staff staff = staffService.getById(body.getStaffId());
        if (staff == null) {
            return Result.error("员工不存在: " + body.getStaffId());
        }
        if (body.getDelta() == null || body.getDelta() == 0) {
            return Result.error("delta 必须为非 0 值");
        }
        body.setCreateTime(new Date());
        staffPointsLogService.save(body);
        return Result.ok("调整成功");
    }
}