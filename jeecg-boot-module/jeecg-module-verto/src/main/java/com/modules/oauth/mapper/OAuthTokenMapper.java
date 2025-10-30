package com.verto.modules.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.verto.modules.oauth.entity.OAuthToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OAuthTokenMapper extends BaseMapper<OAuthToken> {
}