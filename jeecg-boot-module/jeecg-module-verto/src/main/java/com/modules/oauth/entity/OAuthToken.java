package com.verto.modules.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("oauth_token")
public class OAuthToken {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 平台，例如：github */
    private String platform;

    /** 第三方平台的用户唯一标识（如 GitHub 的 id） */
    private String oauthUserId;

    /** 访问令牌与类型 */
    private String accessToken;
    private String tokenType; // Bearer

    /** 授权范围 */
    private String scope;

    /** 过期时间（GitHub Access Token 通常不设置过期；预留字段） */
    private LocalDateTime expiresAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}