package com.verto.modules.oauth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.verto.modules.oauth.entity.OAuthToken;
import com.verto.modules.oauth.entity.OAuthUser;
import com.verto.modules.oauth.mapper.OAuthTokenMapper;
import com.verto.modules.oauth.mapper.OAuthUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthService {
    private final OAuthUserMapper userMapper;
    private final OAuthTokenMapper tokenMapper;
    // 简易内存绑定表：key = systemUserId+"|"+platform, value = thirdUserUuid
    private final Map<String, String> userBindings = new ConcurrentHashMap<>();

    public OAuthService(OAuthUserMapper userMapper, OAuthTokenMapper tokenMapper) {
        this.userMapper = userMapper;
        this.tokenMapper = tokenMapper;
    }

    public OAuthUser upsertUser(String platform, String oauthUserId, String login, String name, String avatarUrl, String email) {
        QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
        qw.eq("platform", platform).eq("oauth_user_id", oauthUserId);
        OAuthUser existing = userMapper.selectOne(qw);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            OAuthUser user = new OAuthUser();
            user.setPlatform(platform);
            user.setOauthUserId(oauthUserId);
            user.setLogin(login);
            user.setName(name);
            user.setAvatarUrl(avatarUrl);
            user.setEmail(email);
            user.setBoundAt(now);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            userMapper.insert(user);
            return user;
        } else {
            existing.setLogin(login);
            existing.setName(name);
            existing.setAvatarUrl(avatarUrl);
            if (StringUtils.hasText(email)) existing.setEmail(email);
            existing.setUpdatedAt(now);
            userMapper.updateById(existing);
            return existing;
        }
    }

    public OAuthToken saveToken(String platform, String oauthUserId, String accessToken, String tokenType, String scope) {
        OAuthToken token = new OAuthToken();
        token.setPlatform(platform);
        token.setOauthUserId(oauthUserId);
        token.setAccessToken(accessToken);
        token.setTokenType(tokenType);
        token.setScope(scope);
        token.setCreatedAt(LocalDateTime.now());
        tokenMapper.insert(token);
        // update user's lastTokenId
        QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
        qw.eq("platform", platform).eq("oauth_user_id", oauthUserId);
        OAuthUser user = userMapper.selectOne(qw);
        if (user != null) {
            user.setLastTokenId(token.getId());
            userMapper.updateById(user);
        }
        return token;
    }

    /**
     * 绑定第三方账号到系统用户
     * @param systemUserId 系统用户ID
     * @param platform 平台（如github）
     * @param thirdUserUuid 第三方用户UUID
     * @return 绑定是否成功
     */
    public boolean bindUserAccount(String systemUserId, String platform, String thirdUserUuid) {
        try {
            // 查找第三方用户
            QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
            qw.eq("platform", platform).eq("oauth_user_id", thirdUserUuid);
            OAuthUser oauthUser = userMapper.selectOne(qw);
            if (oauthUser == null) {
                return false; // 第三方用户不存在
            }
            // 简易内存绑定（后续应落库到 user_oauth_binding 表）
            userBindings.put(systemUserId + "|" + platform, thirdUserUuid);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解绑第三方账号
     * @param systemUserId 系统用户ID
     * @param platform 平台（如github）
     * @return 解绑是否成功
     */
    public boolean unbindUserAccount(String systemUserId, String platform) {
        try {
            userBindings.remove(systemUserId + "|" + platform);
            return true; // 暂时返回成功
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据系统用户和平台，获取最近一次保存的 access_token（从数据库）
     * 仅用于后端代理调用，不回传到前端。
     */
    public String getAccessTokenForSystemUser(String systemUserId, String platform) {
        String thirdUserUuid = userBindings.get(systemUserId + "|" + platform);
        if (!StringUtils.hasText(thirdUserUuid)) {
            return null;
        }
        QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
        qw.eq("platform", platform).eq("oauth_user_id", thirdUserUuid);
        OAuthUser user = userMapper.selectOne(qw);
        if (user == null || !StringUtils.hasText(user.getLastTokenId())) {
            return null;
        }
        OAuthToken token = tokenMapper.selectById(user.getLastTokenId());
        return token != null ? token.getAccessToken() : null;
    }

    /**
     * 获取用户绑定的第三方账号信息
     * @param systemUserId 系统用户ID
     * @return 绑定信息Map
     */
    public Map<String, Object> getUserBindings(String systemUserId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String githubUuid = userBindings.get(systemUserId + "|" + "github");
            if (StringUtils.hasText(githubUuid)) {
                QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
                qw.eq("platform", "github").eq("oauth_user_id", githubUuid);
                OAuthUser oauthUser = userMapper.selectOne(qw);
                if (oauthUser != null) {
                    Map<String, Object> githubBinding = new HashMap<>();
                    githubBinding.put("platform", "github");
                    githubBinding.put("username", oauthUser.getLogin());
                    githubBinding.put("avatar", oauthUser.getAvatarUrl());
                    githubBinding.put("bound", true);
                    githubBinding.put("uuid", oauthUser.getOauthUserId());
                    result.put("github", githubBinding);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}