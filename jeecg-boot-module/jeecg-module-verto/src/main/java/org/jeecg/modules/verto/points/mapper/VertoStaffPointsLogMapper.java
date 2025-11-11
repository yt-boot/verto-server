package org.jeecg.modules.verto.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.verto.points.entity.VertoStaffPointsLog;

import java.util.List;

@Mapper
public interface VertoStaffPointsLogMapper extends BaseMapper<VertoStaffPointsLog> {

    @Select("SELECT COALESCE(SUM(delta),0) FROM verto_staff_points_log WHERE staff_id = #{staffId}")
    Integer sumPointsByStaff(@Param("staffId") String staffId);

    @Select("SELECT id, staff_id AS staffId, delta, event_type AS eventType, source_type AS sourceType, source_id AS sourceId, source_name AS sourceName, remark, create_time AS createTime " +
            "FROM verto_staff_points_log WHERE staff_id = #{staffId} " +
            "ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<VertoStaffPointsLog> listByStaff(@Param("staffId") String staffId, @Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT id, staff_id AS staffId, delta, event_type AS eventType, source_type AS sourceType, source_id AS sourceId, source_name AS sourceName, remark, create_time AS createTime " +
            "FROM verto_staff_points_log ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<VertoStaffPointsLog> listAll(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM verto_staff_points_log WHERE staff_id = #{staffId}")
    Integer countByStaff(@Param("staffId") String staffId);

    @Select("SELECT COUNT(*) FROM verto_staff_points_log")
    Integer countAll();
}