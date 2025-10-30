package org.jeecg.modules.verto.staff.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.verto.staff.dto.StaffPointsAggDTO;
import org.jeecg.modules.verto.staff.entity.StaffPointsLog;

import java.util.List;

@Mapper
public interface StaffPointsLogMapper extends BaseMapper<StaffPointsLog> {

    @Select("SELECT COALESCE(SUM(delta),0) FROM staff_points_log WHERE staff_id = #{staffId}")
    Integer sumByStaffId(@Param("staffId") String staffId);

    @Select({
            "<script>",
            "SELECT staff_id AS staffId, COALESCE(SUM(delta),0) AS totalPoints",
            "FROM staff_points_log",
            "WHERE staff_id IN",
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>",
            "GROUP BY staff_id",
            "</script>"
    })
    List<StaffPointsAggDTO> sumByStaffIds(@Param("ids") List<String> staffIds);
}