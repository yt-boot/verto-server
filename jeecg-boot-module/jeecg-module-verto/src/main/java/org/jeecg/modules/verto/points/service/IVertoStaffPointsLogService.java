package org.jeecg.modules.verto.points.service;

import org.jeecg.modules.verto.points.entity.VertoStaffPointsLog;

import java.util.List;

public interface IVertoStaffPointsLogService {
    Integer getTotalPoints(String staffId);
    List<VertoStaffPointsLog> getLogsByStaff(String staffId, int pageNo, int pageSize);
    List<VertoStaffPointsLog> getAllLogs(int pageNo, int pageSize);
    void adjustPoints(String staffId, int delta, String remark, String sourceType, String sourceId, String eventType);
}