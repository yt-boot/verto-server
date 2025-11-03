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

    /**
     * 删除指定系统用户在指定平台的所有 access_token 记录（通过绑定关系找到对应的 oauth_user_id）。
     * 用于“根据 sysUser.getId() 删除对应的 access_token”。
     */
    boolean deleteAccessTokensBySystemUser(String systemUserId, String platform);

    /**
     * 清理某第三方用户在某平台的历史 token，仅保留最近一个（最新创建的或 lastTokenId 指向的）。
     * 用于在回调保存 token 后，保证“表里仅有一个对应的 access_token”。
     */
    void cleanupTokensForOauthUser(String platform, String oauthUserId);
}