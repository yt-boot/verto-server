package com.verto.modules.staff.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.verto.modules.staff.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人员管理Mapper接口
 * 
 * @author verto
 * @since 2024-01-27
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {

}