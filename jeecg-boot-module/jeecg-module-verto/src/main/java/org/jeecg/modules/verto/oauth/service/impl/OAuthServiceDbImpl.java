package org.jeecg.modules.verto.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jeecg.modules.verto.oauth.entity.OAuthBinding;
import org.jeecg.modules.verto.oauth.entity.OAuthToken;
import org.jeecg.modules.verto.oauth.entity.OAuthUser;
import org.jeecg.modules.verto.oauth.mapper.OAuthBindingMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthTokenMapper;
import org.jeecg.modules.verto.oauth.mapper.OAuthUserMapper;
import org.jeecg.modules.verto.oauth.service.IOAuthService;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于数据库持久化的 OAuthService 实现
 * 注意：通过 @Configuration 提供为 @Primary Bean，替换原 @Service 实现的注入使用。
 */
@RequiredArgsConstructor
public class OAuthServiceDbImpl implements IOAuthService {
    private static final Logger log = LoggerFactory.getLogger(OAuthServiceDbImpl.class);

    private final OAuthUserMapper userMapper;
    private final OAuthTokenMapper tokenMapper;
    private final OAuthBindingMapper bindingMapper;

    @Override
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

    @Override
    public OAuthToken saveToken(String platform, String oauthUserId, String accessToken, String tokenType, String scope) {
        OAuthToken token = new OAuthToken();
        token.setPlatform(platform);
        token.setOauthUserId(oauthUserId);
        token.setAccessToken(accessToken);
        token.setTokenType(tokenType);
        token.setScope(scope);
        token.setCreatedAt(LocalDateTime.now());
        tokenMapper.insert(token);
        // 更新用户最近一次绑定的 tokenId
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
            QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
            qw.eq("platform", platform).eq("oauth_user_id", thirdUserUuid);
            OAuthUser oauthUser = userMapper.selectOne(qw);
            if (oauthUser == null) {
                return false;
            }
            OAuthBinding existing = bindingMapper.selectOne(
                    new QueryWrapper<OAuthBinding>()
                            .eq("system_user_id", systemUserId)
                            .eq("platform", platform)
            );
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
            log.info("unbindUserAccount: systemUserId={}, platform={}", systemUserId, platform);
            bindingMapper.delete(
                    new QueryWrapper<OAuthBinding>()
                            .eq("system_user_id", systemUserId)
                            .eq("platform", platform)
            );
            log.info("unbindUserAccount: deleted binding for systemUserId={}, platform={}", systemUserId, platform);
            return true;
        } catch (Exception e) {
            log.error("unbindUserAccount error: systemUserId={}, platform={}, msg={}", systemUserId, platform, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getAccessTokenForSystemUser(String systemUserId, String platform) {
        log.warn("getAccessTokenForSystemUser: systemUserId={}, platform={}", systemUserId, platform);
        OAuthBinding binding = bindingMapper.selectOne(
                new QueryWrapper<OAuthBinding>()
                        .eq("system_user_id", systemUserId)
                        .eq("platform", platform)
        );
        if (binding == null) {
            log.warn("getAccessTokenForSystemUser: no binding found for systemUserId={}, platform={}", systemUserId, platform);
            return null;
        }
        log.debug("getAccessTokenForSystemUser: binding found -> id={}, systemUserId={}, platform={}, oauthUserId={}",
                binding.getId(), binding.getSystemUserId(), binding.getPlatform(), binding.getOauthUserId());
        if (!StringUtils.hasText(binding.getOauthUserId())) {
            log.warn("getAccessTokenForSystemUser: binding.oauthUserId is empty for systemUserId={}, platform={}", systemUserId, platform);
            return null;
        }
        QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
        qw.eq("platform", platform).eq("oauth_user_id", binding.getOauthUserId());
        OAuthUser user = userMapper.selectOne(qw);
        if (user == null) {
            log.warn("getAccessTokenForSystemUser: oauth user not found. platform={}, oauthUserId={}", platform, binding.getOauthUserId());
            return null;
        }
        log.debug("getAccessTokenForSystemUser: oauth user -> id={}, login={}, lastTokenId={}",
                user.getId(), user.getLogin(), user.getLastTokenId());
        OAuthToken token = null;
        if (user.getLastTokenId() == null) {
            log.warn("getAccessTokenForSystemUser: lastTokenId is empty for oauthUserId={} (platform={}). Trying fallback to latest token...", binding.getOauthUserId(), platform);
        } else {
            token = tokenMapper.selectById(user.getLastTokenId());
            if (token == null) {
                log.warn("getAccessTokenForSystemUser: token not found by lastTokenId={} (oauthUserId={}, platform={}). Trying fallback to latest token...", user.getLastTokenId(), binding.getOauthUserId(), platform);
            }
        }
        // Fallback: pick latest token by created_at if lastTokenId is missing or invalid
        if (token == null) {
            token = tokenMapper.selectOne(new QueryWrapper<OAuthToken>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", binding.getOauthUserId())
                    .orderByDesc("created_at")
                    .last("limit 1"));
            if (token == null) {
                log.warn("getAccessTokenForSystemUser: fallback query also found no token for oauthUserId={} (platform={})", binding.getOauthUserId(), platform);
                return null;
            } else {
                log.info("getAccessTokenForSystemUser: using fallback latest token id={} (oauthUserId={}, platform={})", token.getId(), binding.getOauthUserId(), platform);
            }
        }
        String tokenPreview = token.getAccessToken() == null ? "null" : (token.getAccessToken().length() <= 8 ? token.getAccessToken() : token.getAccessToken().substring(0, 4) + "..." + token.getAccessToken().substring(token.getAccessToken().length()-4));
        log.info("getAccessTokenForSystemUser: token resolved -> id={}, type={}, scope={}, createdAt={}, accessToken(len={}, preview={})",
                token.getId(), token.getTokenType(), token.getScope(), token.getCreatedAt(),
                token.getAccessToken() == null ? 0 : token.getAccessToken().length(), tokenPreview);
        return token.getAccessToken();
    }

    @Override
    public Map<String, Object> getUserBindings(String systemUserId) {
        Map<String, Object> result = new HashMap<>();
        try {
            OAuthBinding gitlabBindingRec = bindingMapper.selectOne(
                    new QueryWrapper<OAuthBinding>()
                            .eq("system_user_id", systemUserId)
                            .eq("platform", "gitlab")
            );
            if (gitlabBindingRec != null && StringUtils.hasText(gitlabBindingRec.getOauthUserId())) {
                QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
                qw.eq("platform", "gitlab").eq("oauth_user_id", gitlabBindingRec.getOauthUserId());
                OAuthUser oauthUser = userMapper.selectOne(qw);
                if (oauthUser != null) {
                    Map<String, Object> gitlabBinding = new HashMap<>();
                    gitlabBinding.put("platform", "gitlab");
                    gitlabBinding.put("username", oauthUser.getLogin());
                    gitlabBinding.put("avatar", oauthUser.getAvatarUrl());
                    gitlabBinding.put("bound", true);
                    gitlabBinding.put("uuid", oauthUser.getOauthUserId());
                    result.put("gitlab", gitlabBinding);
                }
            }
            OAuthBinding githubBindingRec = bindingMapper.selectOne(
                    new QueryWrapper<OAuthBinding>()
                            .eq("system_user_id", systemUserId)
                            .eq("platform", "github")
            );
            if (githubBindingRec != null && StringUtils.hasText(githubBindingRec.getOauthUserId())) {
                QueryWrapper<OAuthUser> qw = new QueryWrapper<>();
                qw.eq("platform", "github").eq("oauth_user_id", githubBindingRec.getOauthUserId());
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

    @Override
    public boolean deleteAccessTokensBySystemUser(String systemUserId, String platform) {
        try {
            OAuthBinding binding = bindingMapper.selectOne(
                    new QueryWrapper<OAuthBinding>()
                            .eq("system_user_id", systemUserId)
                            .eq("platform", platform)
            );
            if (binding == null || !StringUtils.hasText(binding.getOauthUserId())) {
                return false;
            }
            // 删除该第三方用户在该平台下的所有 token
            int deleted = tokenMapper.delete(new QueryWrapper<OAuthToken>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", binding.getOauthUserId())
            );
            // 同步 OAuthUser.lastTokenId 置空
            OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", binding.getOauthUserId())
            );
            if (oauthUser != null) {
                oauthUser.setLastTokenId(null);
                userMapper.updateById(oauthUser);
            }
            return deleted > 0;
        } catch (Exception e) {
            log.error("deleteAccessTokensBySystemUser error: systemUserId={}, platform={}, msg={}", systemUserId, platform, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void cleanupTokensForOauthUser(String platform, String oauthUserId) {
        try {
            // 找到最新的 token（按 created_at 降序）
            java.util.List<OAuthToken> tokens = tokenMapper.selectList(new QueryWrapper<OAuthToken>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", oauthUserId)
                    .orderByDesc("created_at")
            );
            if (tokens == null || tokens.isEmpty()) {
                return;
            }
            OAuthToken newest = tokens.get(0);
            // 删除其余旧 token
            for (int i = 1; i < tokens.size(); i++) {
                tokenMapper.deleteById(tokens.get(i).getId());
            }
            // 同步 OAuthUser.lastTokenId 指向最新 token
            OAuthUser oauthUser = userMapper.selectOne(new QueryWrapper<OAuthUser>()
                    .eq("platform", platform)
                    .eq("oauth_user_id", oauthUserId)
            );
            if (oauthUser != null) {
                oauthUser.setLastTokenId(newest.getId());
                userMapper.updateById(oauthUser);
            }
        } catch (Exception e) {
            log.error("cleanupTokensForOauthUser error: platform={}, oauthUserId={}, msg={}", platform, oauthUserId, e.getMessage(), e);
        }
    }
}