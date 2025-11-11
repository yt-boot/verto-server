package org.jeecg.modules.verto.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.verto.points.entity.VertoStaff;

import java.util.List;

@Mapper
public interface VertoStaffMapper extends BaseMapper<VertoStaff> {

    @Select("SELECT id, name, employee_no AS employeeNo, email, phone, status FROM verto_staff " +
            "WHERE (#{name} IS NULL OR name LIKE CONCAT('%', #{name}, '%')) ORDER BY name ASC")
    List<VertoStaff> listByName(@Param("name") String name);
}