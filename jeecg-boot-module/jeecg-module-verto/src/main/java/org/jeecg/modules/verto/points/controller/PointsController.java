package org.jeecg.modules.verto.points.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.points.entity.VertoStaff;
import org.jeecg.modules.verto.points.entity.VertoStaffPointsLog;
import org.jeecg.modules.verto.points.dto.PointsAdjustDTO;
import org.jeecg.modules.verto.points.mapper.VertoStaffMapper;
import org.jeecg.modules.verto.points.mapper.VertoStaffPointsLogMapper;
import org.jeecg.modules.verto.points.service.IVertoStaffPointsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 积分管理独立接口
 * 路径前缀：/verto/points
 */
@RestController
@RequestMapping("/verto/points")
public class PointsController {

    // 使用按类型注入，避免 @Resource 基于字段名的注入导致与 staff 模块的 StaffMapper Bean 冲突
    @Autowired
    private VertoStaffMapper vertoStaffMapper;

    @Autowired
    private IVertoStaffPointsLogService pointsService;

    @Autowired
    private VertoStaffPointsLogMapper pointsLogMapper;

    /**
     * 人员列表（支持名称模糊查询）
     */
    @GetMapping("/staff/list")
    public Result<IPage<VertoStaff>> staffList(@RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize) {
        QueryWrapper<VertoStaff> query = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            query.like("name", name);
        }
        query.orderByAsc("name");
        IPage<VertoStaff> pageList = vertoStaffMapper.selectPage(new Page<>(pageNo, pageSize), query);
        return Result.OK(pageList);
    }

    /**
     * 人员积分总和
     */
    @GetMapping("/summary")
    public Result<Integer> pointsSummary(@RequestParam("staffId") String staffId) {
        Integer total = pointsService.getTotalPoints(staffId);
        return Result.OK(total);
    }

    /**
     * 人员积分流水
     */
    @GetMapping("/logs")
    public Result<IPage<VertoStaffPointsLog>> pointsLogs(@RequestParam("staffId") String staffId,
                                                         @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        int offset = Math.max(pageNo - 1, 0) * Math.max(pageSize, 1);
        int limit = Math.max(pageSize, 1);
        List<VertoStaffPointsLog> list = pointsService.getLogsByStaff(staffId, pageNo, pageSize);
        Integer total = pointsLogMapper.countByStaff(staffId);
        Page<VertoStaffPointsLog> page = new Page<>(pageNo, pageSize);
        page.setRecords(list);
        page.setTotal(total == null ? 0 : total);
        return Result.OK(page);
    }

    /**
     * 全员积分流水
     */
    @GetMapping("/logs/all")
    public Result<IPage<VertoStaffPointsLog>> pointsLogsAll(@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        int offset = Math.max(pageNo - 1, 0) * Math.max(pageSize, 1);
        int limit = Math.max(pageSize, 1);
        List<VertoStaffPointsLog> list = pointsService.getAllLogs(pageNo, pageSize);
        Integer total = pointsLogMapper.countAll();
        Page<VertoStaffPointsLog> page = new Page<>(pageNo, pageSize);
        page.setRecords(list);
        page.setTotal(total == null ? 0 : total);
        return Result.OK(page);
    }

    /**
     * 积分调整
     */
    @PostMapping("/adjust")
    public Result<String> adjust(
            @RequestBody(required = false) PointsAdjustDTO body,
            @RequestParam(value = "staffId", required = false) String staffId,
            @RequestParam(value = "delta", required = false) Integer delta,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "sourceType", required = false) String sourceType,
            @RequestParam(value = "sourceId", required = false) String sourceId,
            @RequestParam(value = "eventType", required = false) String eventType
    ) {
        // 优先使用 JSON Body，其次兼容表单/查询参数
        String effectiveStaffId = (body != null && StringUtils.isNotBlank(body.getStaffId())) ? body.getStaffId() : staffId;
        Integer effectiveDelta = (body != null && body.getDelta() != null) ? body.getDelta() : delta;
        String effectiveRemark = (body != null && StringUtils.isNotBlank(body.getRemark())) ? body.getRemark() : remark;
        String effectiveSourceType = (body != null && StringUtils.isNotBlank(body.getSourceType())) ? body.getSourceType() : sourceType;
        String effectiveSourceId = (body != null && StringUtils.isNotBlank(body.getSourceId())) ? body.getSourceId() : sourceId;
        String effectiveEventType = (body != null && StringUtils.isNotBlank(body.getEventType())) ? body.getEventType() : eventType;

        if (StringUtils.isBlank(effectiveStaffId) || effectiveDelta == null) {
            return Result.error("参数不足：staffId/delta");
        }
        pointsService.adjustPoints(effectiveStaffId, effectiveDelta, effectiveRemark, effectiveSourceType, effectiveSourceId, effectiveEventType);
        return Result.OK("调整成功");
    }
}