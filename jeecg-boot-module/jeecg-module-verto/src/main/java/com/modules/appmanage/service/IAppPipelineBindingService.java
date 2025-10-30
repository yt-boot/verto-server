package com.verto.modules.appmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.verto.modules.appmanage.entity.AppPipelineBinding;

import java.util.List;

/**
 * Service: 应用流水线绑定
 */
public interface IAppPipelineBindingService extends IService<AppPipelineBinding> {

    /**
     * 按应用与环境查询绑定列表（按更新时间倒序）
     */
    List<AppPipelineBinding> listByAppAndEnv(String appId, String environment);
}