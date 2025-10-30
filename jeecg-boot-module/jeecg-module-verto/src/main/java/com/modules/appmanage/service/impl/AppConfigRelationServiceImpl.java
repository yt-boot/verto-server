package com.verto.modules.appmanage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.appmanage.entity.AppConfigRelation;
import com.verto.modules.appmanage.mapper.AppConfigRelationMapper;
import com.verto.modules.appmanage.service.IAppConfigRelationService;
import org.springframework.stereotype.Service;

@Service
public class AppConfigRelationServiceImpl extends ServiceImpl<AppConfigRelationMapper, AppConfigRelation> implements IAppConfigRelationService {
}