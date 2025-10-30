package org.jeecg.modules.verto.appmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.verto.appmanage.entity.AppManage;
import org.jeecg.modules.verto.appmanage.mapper.AppManageMapper;
import org.jeecg.modules.verto.appmanage.service.IAppManageService;
import org.springframework.stereotype.Service;

@Service
public class AppManageServiceImpl extends ServiceImpl<AppManageMapper, AppManage> implements IAppManageService {
}