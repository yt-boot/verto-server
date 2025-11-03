package org.jeecg.modules.verto.oauth.service;

import org.jeecg.modules.verto.oauth.entity.OAuthToken;
import org.jeecg.modules.verto.oauth.entity.OAuthUser;

import java.util.Map;

public interface IOAuthService {
    OAuthUser upsertUser(String platform, String oauthUserId, String login, String name, String avatarUrl, String email);
    OAuthToken saveToken(String platform, String oauthUserId, String accessToken, String tokenType, String scope);

    boolean bindUserAccount(String systemUserId, String platform, String thirdUserUuid);
    boolean unbindUserAccount(String systemUserId, String platform);
    String getAccessTokenForSystemUser(String systemUserId, String platform);
    Map<String, Object> getUserBindings(String systemUserId);
}