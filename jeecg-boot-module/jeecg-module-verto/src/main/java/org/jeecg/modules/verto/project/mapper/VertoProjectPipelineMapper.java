package org.jeecg.modules.verto.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.verto.project.entity.VertoProjectPipeline;

@Mapper
public interface VertoProjectPipelineMapper extends BaseMapper<VertoProjectPipeline> {
    @Select("SELECT COALESCE(MAX(build_number),0) FROM verto_project_pipeline WHERE pipeline_id = #{pipelineId}")
    Integer getMaxBuildNumber(String pipelineId);
}