package org.jeecg.modules.verto.oauth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("oauth_binding")
@Schema(description = "系统用户与第三方OAuth用户的绑定关系")
public class OAuthBinding implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** 系统用户ID */
    private String systemUserId;

    /** 平台：github/gitlab */
    private String platform;

    /** 第三方平台用户唯一标识 */
    private String oauthUserId;

    /** 绑定/更新时间 */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}