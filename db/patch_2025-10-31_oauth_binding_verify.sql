-- 验证 oauth_binding 表结构与数据的脚本

-- 查看表结构（含索引）
SHOW CREATE TABLE `oauth_binding`;

-- 验证索引是否存在
SHOW INDEX FROM `oauth_binding`;

-- 查看外键约束（在 MySQL 8+ 可以通过 information_schema 查询）
SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oauth_binding' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 示例：查看前若干条数据
SELECT * FROM `oauth_binding` ORDER BY `created_at` DESC LIMIT 10;