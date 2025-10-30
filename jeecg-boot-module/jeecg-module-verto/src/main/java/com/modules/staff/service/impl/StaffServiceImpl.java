package com.verto.modules.staff.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.staff.entity.Staff;
import com.verto.modules.staff.mapper.StaffMapper;
import com.verto.modules.staff.service.IStaffService;
import org.springframework.stereotype.Service;

/**
 * 人员管理Service实现类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {

}