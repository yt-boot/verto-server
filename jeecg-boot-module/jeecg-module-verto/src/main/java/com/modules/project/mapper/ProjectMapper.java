package com.verto.modules.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.verto.modules.project.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目管理Mapper接口
 * 
 * @author verto
 * @since 2024-01-27
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {

}