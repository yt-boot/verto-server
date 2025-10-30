package org.jeecg.modules.verto.staff.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.verto.staff.entity.StaffPointsLog;

import java.util.Collection;
import java.util.Map;

public interface IStaffPointsLogService extends IService<StaffPointsLog> {
    Integer getTotalPointsByStaffId(String staffId);
    Map<String, Integer> getTotalPointsByStaffIds(Collection<String> staffIds);
}