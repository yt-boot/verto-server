package org.jeecg.modules.verto.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("oauth_token")
@Schema(description = "第三方OAuth令牌")
public class OAuthToken implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 平台，例如：gitlab、github */
    private String platform;

    /** 第三方平台的用户唯一标识（如 GitHub/GitLab 的 id） */
    private String oauthUserId;

    /** 访问令牌与类型 */
    private String accessToken;
    private String tokenType; // Bearer

    /** 授权范围 */
    private String scope;

    /** 过期时间（部分平台access token可能有过期时间；预留字段） */
    private LocalDateTime expiresAt;

    /** 创建时间 */
    private LocalDateTime createdAt;
}