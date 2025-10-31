package org.jeecg.modules.verto.material.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.verto.material.entity.MaterialTemplate;
import org.jeecg.modules.verto.material.mapper.MaterialTemplateMapper;
import org.jeecg.modules.verto.material.service.IMaterialTemplateService;
import org.springframework.stereotype.Service;

@Service
public class MaterialTemplateServiceImpl extends ServiceImpl<MaterialTemplateMapper, MaterialTemplate>
        implements IMaterialTemplateService {
}