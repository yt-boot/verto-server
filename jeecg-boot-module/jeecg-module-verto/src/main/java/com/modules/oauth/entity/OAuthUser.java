package com.verto.modules.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("oauth_user")
public class OAuthUser {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 平台，例如：github */
    private String platform;

    /** 第三方平台的用户唯一标识（如 GitHub 的 id） */
    private String oauthUserId;

    /** 登录名（如 GitHub login） */
    private String login;

    /** 显示名称 */
    private String name;

    /** 头像 */
    private String avatarUrl;

    /** 邮箱 */
    private String email;

    /** 绑定时间 */
    private LocalDateTime boundAt;

    /** 创建/更新时间 */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 最近一次绑定的 tokenId（可选，如果你希望建立关联） */
    @TableField(exist = true)
    private String lastTokenId;
}