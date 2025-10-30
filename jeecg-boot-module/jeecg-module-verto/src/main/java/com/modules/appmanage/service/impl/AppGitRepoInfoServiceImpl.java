package com.verto.modules.appmanage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.verto.modules.appmanage.entity.AppGitRepoInfo;
import com.verto.modules.appmanage.mapper.AppGitRepoInfoMapper;
import com.verto.modules.appmanage.service.IAppGitRepoInfoService;
import org.springframework.stereotype.Service;

@Service
public class AppGitRepoInfoServiceImpl extends ServiceImpl<AppGitRepoInfoMapper, AppGitRepoInfo> implements IAppGitRepoInfoService {

    @Override
    public boolean upsertByAppId(AppGitRepoInfo info) {
        if (info == null || info.getAppId() == null) {
            return false;
        }
        QueryWrapper<AppGitRepoInfo> qw = new QueryWrapper<>();
        qw.eq("app_id", info.getAppId());
        AppGitRepoInfo existing = getOne(qw, false);
        if (existing != null) {
            info.setId(existing.getId());
            return updateById(info);
        } else {
            return save(info);
        }
    }
}