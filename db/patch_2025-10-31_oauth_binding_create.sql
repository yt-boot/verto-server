-- 创建 oauth_binding 表（系统用户与第三方OAuth用户绑定关系）
-- 说明：仅需执行一次。若表已存在，请跳过本脚本。

SET NAMES utf8mb4;
START TRANSACTION;

CREATE TABLE IF NOT EXISTS `oauth_binding` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `system_user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统用户ID',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_platform` (`system_user_id`,`platform`),
  KEY `idx_platform_oauth_user` (`platform`,`oauth_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户与第三方OAuth用户绑定关系表';

COMMIT;