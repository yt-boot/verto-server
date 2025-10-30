package com.verto.modules.staff.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.staff.entity.StaffPointsLog;
import com.verto.modules.staff.entity.dto.StaffPointsAggDTO;
import com.verto.modules.staff.mapper.StaffPointsLogMapper;
import com.verto.modules.staff.service.IStaffPointsLogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaffPointsLogServiceImpl extends ServiceImpl<StaffPointsLogMapper, StaffPointsLog> implements IStaffPointsLogService {

    @Override
    public Integer getTotalPointsByStaffId(String staffId) {
        Integer sum = baseMapper.sumByStaffId(staffId);
        return sum == null ? 0 : sum;
    }

    @Override
    public Map<String, Integer> getTotalPointsMapByStaffIds(List<String> staffIds) {
        Map<String, Integer> map = new HashMap<>();
        if (staffIds == null || staffIds.isEmpty()) {
            return map;
        }
        List<StaffPointsAggDTO> list = baseMapper.sumByStaffIds(staffIds);
        for (StaffPointsAggDTO dto : list) {
            map.put(dto.getStaffId(), dto.getTotalPoints() == null ? 0 : dto.getTotalPoints());
        }
        // 对没有流水的员工补0
        for (String id : staffIds) {
            map.putIfAbsent(id, 0);
        }
        return map;
    }
}