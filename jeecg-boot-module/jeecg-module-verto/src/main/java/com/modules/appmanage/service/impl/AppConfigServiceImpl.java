package com.verto.modules.appmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.appmanage.entity.AppConfig;
import com.verto.modules.appmanage.mapper.AppConfigMapper;
import com.verto.modules.appmanage.service.IAppConfigService;
import org.springframework.stereotype.Service;

/**
 * AppConfig Service Implementation
 */
@Service
public class AppConfigServiceImpl extends ServiceImpl<AppConfigMapper, AppConfig> implements IAppConfigService {
}