-- 新增应用附加信息字段（技术栈、备注等），用于保存较大 JSON 文本
ALTER TABLE `app_manage`
  ADD COLUMN `extra_info` LONGTEXT COLLATE utf8mb4_unicode_ci COMMENT '应用附加信息(JSON：技术栈、备注等)' AFTER `managers`;

-- 回滚语句（如需）
-- ALTER TABLE `app_manage` DROP COLUMN `extra_info`;