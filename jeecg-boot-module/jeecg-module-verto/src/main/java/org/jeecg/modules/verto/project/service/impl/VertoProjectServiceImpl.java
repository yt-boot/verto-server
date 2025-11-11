package org.jeecg.modules.verto.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.jeecg.modules.verto.project.entity.VertoProject;
import org.jeecg.modules.verto.project.mapper.VertoProjectMapper;
import org.jeecg.modules.verto.project.service.IVertoProjectService;

@Service
public class VertoProjectServiceImpl extends ServiceImpl<VertoProjectMapper, VertoProject> implements IVertoProjectService {
}