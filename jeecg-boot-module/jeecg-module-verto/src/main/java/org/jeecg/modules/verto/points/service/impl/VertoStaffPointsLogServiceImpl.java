package org.jeecg.modules.verto.points.service.impl;

import org.jeecg.modules.verto.points.entity.VertoStaffPointsLog;
import org.jeecg.modules.verto.points.mapper.VertoStaffPointsLogMapper;
import org.jeecg.modules.verto.points.service.IVertoStaffPointsLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class VertoStaffPointsLogServiceImpl implements IVertoStaffPointsLogService {

    // 使用按类型注入，避免 @Resource 基于字段名的注入造成歧义
    @Autowired
    private VertoStaffPointsLogMapper logMapper;

    @Override
    public Integer getTotalPoints(String staffId) {
        Integer sum = logMapper.sumPointsByStaff(staffId);
        return sum == null ? 0 : sum;
    }

    @Override
    public List<VertoStaffPointsLog> getLogsByStaff(String staffId, int pageNo, int pageSize) {
        int offset = Math.max((pageNo - 1), 0) * Math.max(pageSize, 1);
        int limit = Math.max(pageSize, 1);
        return logMapper.listByStaff(staffId, limit, offset);
    }

    @Override
    public List<VertoStaffPointsLog> getAllLogs(int pageNo, int pageSize) {
        int offset = Math.max((pageNo - 1), 0) * Math.max(pageSize, 1);
        int limit = Math.max(pageSize, 1);
        return logMapper.listAll(limit, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustPoints(String staffId, int delta, String remark, String sourceType, String sourceId, String eventType) {
        VertoStaffPointsLog log = new VertoStaffPointsLog();
        log.setId(UUID.randomUUID().toString().replace("-", ""));
        log.setStaffId(staffId);
        log.setDelta(delta);
        log.setRemark(remark);
        log.setSourceType(sourceType);
        log.setSourceId(sourceId);
        log.setSourceName(null);
        log.setEventType(eventType);
        // create_time 由数据库默认值填充
        logMapper.insert(log);
    }
}