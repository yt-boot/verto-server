package org.jeecg.modules.verto.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.verto.oauth.entity.OAuthUser;

@Mapper
public interface OAuthUserMapper extends BaseMapper<OAuthUser> {
    
    @Select("SELECT * FROM oauth_user WHERE platform = #{platform} AND oauth_user_id = #{oauthUserId} LIMIT 1")
    OAuthUser findByPlatformAndOauthUserId(@Param("platform") String platform, @Param("oauthUserId") String oauthUserId);
}