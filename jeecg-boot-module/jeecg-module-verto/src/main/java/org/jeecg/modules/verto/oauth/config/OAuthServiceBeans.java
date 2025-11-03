package org.jeecg.modules.verto.oauth.config;

import lombok.RequiredArgsConstructor;
import org.jeecg.modules.verto.oauth.mapper.OAuthBindingMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthTokenMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthUserMapper;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.jeecg.modules.verto.oauth.service.impl.OAuthServiceDbImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class OAuthServiceBeans {

    private final OAuthUserMapper userMapper;
    private final OAuthTokenMapper tokenMapper;
    private final OAuthBindingMapper bindingMapper;

    /**
     * 覆盖默认的 IOAuthService 注入，提供基于数据库持久化的实现。
     */
    @Bean
    @Primary
    public IOAuthService oAuthService() {
        return new OAuthServiceDbImpl(userMapper, tokenMapper, bindingMapper);
    }
}