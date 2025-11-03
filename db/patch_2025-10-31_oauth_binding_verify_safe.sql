-- 安全验证脚本：不会因表不存在而报错

-- 当前数据库名
SELECT DATABASE() AS current_database;

-- 检查 oauth_binding 是否存在
SELECT CASE WHEN COUNT(*) > 0 THEN 'exists' ELSE 'not_exists' END AS oauth_binding_exists
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oauth_binding';

-- 当上面返回 exists 时，再手动执行以下两条（避免因表不存在导致错误）：
-- SHOW CREATE TABLE `oauth_binding`;
-- SHOW INDEX FROM `oauth_binding`;

-- 外键约束检查（不存在时返回空集，不会报错）
SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oauth_binding' AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 示例数据查看（若表不存在请跳过）
-- SELECT * FROM `oauth_binding` ORDER BY `created_at` DESC LIMIT 10;