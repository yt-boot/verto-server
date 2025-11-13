package org.jeecg.modules.verto.pipeline.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.jeecg.modules.verto.pipeline.entity.VertoPipeline;
import org.jeecg.modules.verto.pipeline.mapper.VertoPipelineMapper;
import org.jeecg.modules.verto.pipeline.service.IVertoPipelineService;

@Service
public class VertoPipelineServiceImpl extends ServiceImpl<VertoPipelineMapper, VertoPipeline>
        implements IVertoPipelineService {
}