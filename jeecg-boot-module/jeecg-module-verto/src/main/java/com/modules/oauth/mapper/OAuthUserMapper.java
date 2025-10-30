package com.verto.modules.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.verto.modules.oauth.entity.OAuthUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuthUserMapper extends BaseMapper<OAuthUser> {
}