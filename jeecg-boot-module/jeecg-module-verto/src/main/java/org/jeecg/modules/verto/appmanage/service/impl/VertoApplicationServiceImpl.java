package org.jeecg.modules.verto.appmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.jeecg.modules.verto.appmanage.entity.VertoApplication;
import org.jeecg.modules.verto.appmanage.mapper.VertoApplicationMapper;
import org.jeecg.modules.verto.appmanage.service.IVertoApplicationService;

@Service
public class VertoApplicationServiceImpl extends ServiceImpl<VertoApplicationMapper, VertoApplication> implements IVertoApplicationService {
}