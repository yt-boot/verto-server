package com.verto.modules.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.project.entity.Project;
import com.verto.modules.project.mapper.ProjectMapper;
import com.verto.modules.project.service.IProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目管理Service实现类
 * 
 * @author verto
 * @since 2024-01-27
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

}