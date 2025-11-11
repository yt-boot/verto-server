package org.jeecg.modules.verto.staff.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.verto.staff.entity.Staff;
import org.jeecg.modules.verto.staff.mapper.StaffMapper;
import org.jeecg.modules.verto.staff.service.IStaffService;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {
}