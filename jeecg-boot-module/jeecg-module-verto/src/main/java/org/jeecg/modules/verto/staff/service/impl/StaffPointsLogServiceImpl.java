package org.jeecg.modules.verto.staff.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.verto.staff.dto.StaffPointsAggDTO;
import org.jeecg.modules.verto.staff.entity.StaffPointsLog;
import org.jeecg.modules.verto.staff.mapper.StaffPointsLogMapper;
import org.jeecg.modules.verto.staff.service.IStaffPointsLogService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaffPointsLogServiceImpl extends ServiceImpl<StaffPointsLogMapper, StaffPointsLog> implements IStaffPointsLogService {

    @Override
    public Integer getTotalPointsByStaffId(String staffId) {
        Integer sum = this.baseMapper.sumByStaffId(staffId);
        return sum == null ? 0 : sum;
    }

    @Override
    public Map<String, Integer> getTotalPointsByStaffIds(Collection<String> staffIds) {
        if (staffIds == null || staffIds.isEmpty()) {
            return new HashMap<>();
        }
        List<StaffPointsAggDTO> list = this.baseMapper.sumByStaffIds(staffIds.stream().toList());
        Map<String, Integer> map = new HashMap<>();
        for (StaffPointsAggDTO dto : list) {
            map.put(dto.getStaffId(), dto.getTotalPoints());
        }
        return map;
    }
}