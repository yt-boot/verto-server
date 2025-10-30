package com.verto.modules.appmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.appmanage.entity.AppConfigContent;
import com.verto.modules.appmanage.mapper.AppConfigContentMapper;
import com.verto.modules.appmanage.service.IAppConfigContentService;
import org.springframework.stereotype.Service;

@Service
public class AppConfigContentServiceImpl extends ServiceImpl<AppConfigContentMapper, AppConfigContent> implements IAppConfigContentService {
}