package com.verto.modules.staff.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.verto.modules.staff.entity.StaffPointsLog;
import com.verto.modules.staff.entity.dto.StaffPointsAggDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 员工积分流水 Mapper
 */
@Mapper
public interface StaffPointsLogMapper extends BaseMapper<StaffPointsLog> {

    /**
     * 统计单个员工的总积分
     */
    @Select("SELECT COALESCE(SUM(delta),0) FROM staff_points_log WHERE staff_id = #{staffId}")
    Integer sumByStaffId(@Param("staffId") String staffId);

    /**
     * 批量统计员工总积分（返回 staffId 与 totalPoints 列表）
     */
    @Select({
            "<script>",
            "SELECT staff_id AS staffId, COALESCE(SUM(delta),0) AS totalPoints ",
            "FROM staff_points_log ",
            "WHERE staff_id IN ",
            "<foreach item='id' collection='staffIds' open='(' separator=',' close=')'>#{id}</foreach>",
            "GROUP BY staff_id",
            "</script>"
    })
    List<StaffPointsAggDTO> sumByStaffIds(@Param("staffIds") List<String> staffIds);
}