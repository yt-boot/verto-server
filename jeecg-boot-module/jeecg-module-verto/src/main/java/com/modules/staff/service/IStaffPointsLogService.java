package com.verto.modules.staff.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.verto.modules.staff.entity.StaffPointsLog;

import java.util.List;
import java.util.Map;

/**
 * 员工积分流水 Service 接口
 */
public interface IStaffPointsLogService extends IService<StaffPointsLog> {

    /**
     * 获取单个员工的总积分
     */
    Integer getTotalPointsByStaffId(String staffId);

    /**
     * 批量获取员工总积分 Map（key: staffId, value: totalPoints）
     */
    Map<String, Integer> getTotalPointsMapByStaffIds(List<String> staffIds);
}