package org.jeecg.modules.verto.staff.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.staff.dto.PointsLogDTO;
import org.jeecg.modules.verto.staff.dto.StaffDTO;
import org.jeecg.modules.verto.staff.entity.Staff;
import org.jeecg.modules.verto.staff.entity.StaffPointsLog;
import org.jeecg.modules.verto.staff.mapper.StaffPointsLogMapper;
import org.jeecg.modules.verto.staff.service.IStaffPointsLogService;
import org.jeecg.modules.verto.staff.service.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/jeecgboot/verto/staff", "/verto-backend/staff"})
public class StaffPointsController {

    @Autowired
    private IStaffService staffService;
    @Autowired
    private IStaffPointsLogService staffPointsLogService;
    @Autowired
    private StaffPointsLogMapper staffPointsLogMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int pageNo,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String keyword) {
        QueryWrapper<Staff> qw = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.lambda().like(Staff::getName, keyword);
        }
        Page<Staff> page = staffService.page(new Page<>(pageNo, pageSize), qw);
        List<Staff> staffList = page.getRecords();
        List<String> ids = staffList.stream().map(Staff::getId).collect(Collectors.toList());
        Map<String, Integer> pointsMap = ids.isEmpty() ? Collections.emptyMap() : staffPointsLogMapper.sumByStaffIds(ids).stream()
                .collect(Collectors.toMap(
                        org.jeecg.modules.verto.staff.dto.StaffPointsAggDTO::getStaffId,
                        org.jeecg.modules.verto.staff.dto.StaffPointsAggDTO::getTotalPoints
                ));
        List<StaffDTO> dtos = staffList.stream().map(s -> toDTO(s, pointsMap.getOrDefault(s.getId(), 0))).collect(Collectors.toList());
        return Result.OK(dtos);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable String id) {
        Staff s = staffService.getById(id);
        if (s == null) return Result.error("Staff not found");
        Integer points = staffPointsLogMapper.sumByStaffId(id);
        return Result.OK(toDTO(s, points == null ? 0 : points));
    }

    @GetMapping("/points/logs")
    public Result<?> pointsLogs(@RequestParam String staffId) {
        List<StaffPointsLog> logs = staffPointsLogService.list(new QueryWrapper<StaffPointsLog>().lambda()
                .eq(StaffPointsLog::getStaffId, staffId)
                .orderByDesc(StaffPointsLog::getCreateTime));
        List<PointsLogDTO> dtos = logs.stream().map(this::toLogDTO).collect(Collectors.toList());
        return Result.OK(dtos);
    }

    @PostMapping("/points/adjust")
    public Result<?> adjust(@RequestBody Map<String, Object> payload) {
        String staffId = (String) payload.get("staffId");
        Integer delta = ((Number) payload.getOrDefault("delta", 0)).intValue();
        String remark = (String) payload.getOrDefault("remark", "");
        Staff s = staffService.getById(staffId);
        if (s == null) return Result.error("Staff not found");
        StaffPointsLog log = new StaffPointsLog();
        log.setStaffId(staffId);
        log.setEventType("adjust");
        log.setSourceType("manual");
        log.setSourceId(null);
        log.setSourceName(s.getName());
        log.setDelta(delta);
        log.setRemark(remark);
        log.setCreateTime(new Date());
        staffPointsLogService.save(log);
        return Result.OK("Adjusted");
    }

    private StaffDTO toDTO(Staff s, int points) {
        StaffDTO dto = new StaffDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setEmployeeNo(s.getEmployeeNo());
        dto.setEmail(s.getEmail());
        dto.setPhone(s.getPhone());
        dto.setHireDate(formatDate(s.getHireDate()));
        dto.setWorkLocation(s.getWorkLocation());
        dto.setSkills(s.getSkills());
        dto.setStatus(s.getStatus());
        dto.setRemark(s.getRemark());
        dto.setCreateTime(formatDate(s.getCreateTime()));
        dto.setUpdateTime(formatDate(s.getUpdateTime()));
        dto.setPoints(points);
        return dto;
    }

    private PointsLogDTO toLogDTO(StaffPointsLog l) {
        PointsLogDTO dto = new PointsLogDTO();
        dto.setId(l.getId());
        dto.setStaffId(l.getStaffId());
        dto.setEventType(l.getEventType());
        dto.setSourceType(l.getSourceType());
        dto.setSourceId(l.getSourceId());
        dto.setSourceName(l.getSourceName());
        dto.setDelta(l.getDelta());
        dto.setRemark(l.getRemark());
        dto.setCreateTime(formatDate(l.getCreateTime()));
        return dto;
    }

    private String formatDate(Date d) {
        if (d == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }
}