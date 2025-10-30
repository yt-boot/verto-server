package com.verto.modules.appmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.appmanage.entity.AppPipelineBinding;
import com.verto.modules.appmanage.mapper.AppPipelineBindingMapper;
import com.verto.modules.appmanage.service.IAppPipelineBindingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ServiceImpl: 应用流水线绑定
 */
@Service
public class AppPipelineBindingServiceImpl extends ServiceImpl<AppPipelineBindingMapper, AppPipelineBinding>
        implements IAppPipelineBindingService {

    @Override
    public List<AppPipelineBinding> listByAppAndEnv(String appId, String environment) {
        QueryWrapper<AppPipelineBinding> qw = new QueryWrapper<>();
        qw.eq("app_id", appId);
        if (environment != null && !environment.isEmpty()) {
            qw.eq("environment", environment);
        }
        // 优先启用状态，其次更新时间倒序
        qw.orderByDesc("status");
        qw.orderByDesc("update_time");
        return this.list(qw);
    }
}