package org.jeecg.modules.verto.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.verto.oauth.entity.OAuthToken;

@Mapper
public interface OAuthTokenMapper extends BaseMapper<OAuthToken> {
}