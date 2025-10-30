package com.verto.modules.appmanage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.verto.modules.appmanage.entity.AppGitRepoInfo;

public interface IAppGitRepoInfoService extends IService<AppGitRepoInfo> {
    /**
     * Upsert by appId: if exists, update; otherwise, insert.
     */
    boolean upsertByAppId(AppGitRepoInfo info);
}