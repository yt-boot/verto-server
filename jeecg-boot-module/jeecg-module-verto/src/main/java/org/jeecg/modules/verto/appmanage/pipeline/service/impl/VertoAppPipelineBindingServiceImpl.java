package org.jeecg.modules.verto.appmanage.pipeline.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.jeecg.modules.verto.appmanage.pipeline.entity.VertoAppPipelineBinding;
import org.jeecg.modules.verto.appmanage.pipeline.mapper.VertoAppPipelineBindingMapper;
import org.jeecg.modules.verto.appmanage.pipeline.service.IVertoAppPipelineBindingService;

@Service
public class VertoAppPipelineBindingServiceImpl extends ServiceImpl<VertoAppPipelineBindingMapper, VertoAppPipelineBinding>
        implements IVertoAppPipelineBindingService {
}