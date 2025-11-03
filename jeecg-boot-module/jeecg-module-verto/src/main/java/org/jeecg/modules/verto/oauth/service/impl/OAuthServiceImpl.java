package org.jeecg.modules.verto.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.jeecg.modules.verto.oauth.entity.OAuthToken;
import org.jeecg.modules.verto.oauth.entity.OAuthBinding;
import org.jeecg.modules.verto.oauth.entity.OAuthUser;
import org.jeecg.modules.verto.oauth.mapper.OAuthBindingMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthTokenMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthUserMapper;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements IOAuthService {
    private final OAuthUserMapper userMapper;
    private final OAuthTokenMapper tokenMapper;
    private final OAuthBindingMapper bindingMapper;

    @Override
    public OAuthUser upsertUser(String platform, String oauthUserId, String login, String name, String avatarUrl,
            String email) {
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
            if (StringUtils.hasText(email))
                existing.setEmail(email);
            existing.setUpdatedAt(now);
            userMapper.updateById(existing);
            return existing;
        }
    }

    @Override
    public OAuthToken saveToken(String platform, String oauthUserId, String accessToken, String tokenType,
            String scope) {
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

    @Override
    public boolean bindUserAccount(String systemUserId, String platform, String thirdUserUuid) {
        try {
            // 校验第三方用户是否存在
            OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", thirdUserUuid));
            if (oauthUser == null) {
                return false;
            }
            // 查询是否已有绑定记录
            OAuthBinding existing = bindingMapper.selectOne(new QueryWrapper<OAuthBinding>()
                    .eq("system_user_id", systemUserId)
                    .eq("platform", platform));
            LocalDateTime now = LocalDateTime.now();
            if (existing == null) {
                OAuthBinding binding = new OAuthBinding();
                binding.setSystemUserId(systemUserId);
                binding.setPlatform(platform);
                binding.setOauthUserId(thirdUserUuid);
                binding.setCreatedAt(now);
                binding.setUpdatedAt(now);
                bindingMapper.insert(binding);
            } else {
                existing.setOauthUserId(thirdUserUuid);
                existing.setUpdatedAt(now);
                bindingMapper.updateById(existing);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unbindUserAccount(String systemUserId, String platform) {
        try {
            int deleted = bindingMapper.delete(new QueryWrapper<OAuthBinding>()
                    .eq("system_user_id", systemUserId)
                    .eq("platform", platform));
            return deleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getAccessTokenForSystemUser(String systemUserId, String platform) {
        // 查绑定
        OAuthBinding binding = bindingMapper.selectOne(new QueryWrapper<OAuthBinding>()
                .eq("system_user_id", systemUserId)
                .eq("platform", platform));
        if (binding == null || !StringUtils.hasText(binding.getOauthUserId())) {
            return null;
        }
        // 查用户与最后一次token
        OAuthUser user = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                .eq("platform", platform)
                .eq("oauth_user_id", binding.getOauthUserId()));
        if (user != null && user.getLastTokenId() != null) {
            OAuthToken token = tokenMapper.selectById(user.getLastTokenId());
            if (token != null) return token.getAccessToken();
        }
        // 兜底：按最新创建时间取一条token
        OAuthToken latestToken = tokenMapper.selectOne(new QueryWrapper<OAuthToken>()
                .eq("platform", platform)
                .eq("oauth_user_id", binding.getOauthUserId())
                .orderByDesc("created_at")
                .last("limit 1"));
        return latestToken != null ? latestToken.getAccessToken() : null;
    }

    @Override
    public Map<String, Object> getUserBindings(String systemUserId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 查询用户所有绑定记录
            for (String platform : new String[]{"gitlab", "github"}) {
                OAuthBinding binding = bindingMapper.selectOne(new QueryWrapper<OAuthBinding>()
                        .eq("system_user_id", systemUserId)
                        .eq("platform", platform));
                if (binding != null && StringUtils.hasText(binding.getOauthUserId())) {
                    OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                            .eq("platform", platform)
                            .eq("oauth_user_id", binding.getOauthUserId()));
                    if (oauthUser != null) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("platform", platform);
                        item.put("username", oauthUser.getLogin());
                        item.put("avatar", oauthUser.getAvatarUrl());
                        item.put("bound", true);
                        item.put("uuid", oauthUser.getOauthUserId());
                        result.put(platform, item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean deleteAccessTokensBySystemUser(String systemUserId, String platform) {
        try {
            OAuthBinding binding = bindingMapper.selectOne(new QueryWrapper<OAuthBinding>()
                    .eq("system_user_id", systemUserId)
                    .eq("platform", platform));
            if (binding == null || !StringUtils.hasText(binding.getOauthUserId())) {
                return false;
            }
            int deleted = tokenMapper.delete(new QueryWrapper<OAuthToken>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", binding.getOauthUserId()));
            OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", binding.getOauthUserId()));
            if (oauthUser != null) {
                oauthUser.setLastTokenId(null);
                userMapper.updateById(oauthUser);
            }
            return deleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void cleanupTokensForOauthUser(String platform, String oauthUserId) {
        try {
            java.util.List<OAuthToken> tokens = tokenMapper.selectList(new QueryWrapper<OAuthToken>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", oauthUserId)
                    .orderByDesc("created_at"));
            if (tokens == null || tokens.isEmpty()) return;
            OAuthToken newest = tokens.get(0);
            for (int i = 1; i < tokens.size(); i++) {
                tokenMapper.deleteById(tokens.get(i).getId());
            }
            OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", oauthUserId));
            if (oauthUser != null) {
                oauthUser.setLastTokenId(newest.getId());
                userMapper.updateById(oauthUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
