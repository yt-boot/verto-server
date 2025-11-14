-- 为verto_application表添加app_level字段并创建索引的SQL补丁
-- 生成日期：2024-07-16

-- 1. 向verto_application表添加app_level字段
ALTER TABLE `verto_application` 
ADD COLUMN `app_level` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'other' COMMENT '应用等级(level1:一级应用,level2:二级应用,level3:三级应用,other:其他)';

-- 2. 为app_level字段创建索引
ALTER TABLE `verto_application` 
ADD INDEX `idx_app_level` (`app_level`);

-- 3. 插入一条示例数据（包含app_level字段）
INSERT INTO `verto_application` (`id`, `app_name`, `app_description`, `app_level`, `status`) 
VALUES ('test_app_001', '测试应用', '这是一个测试应用', 'level1', 1);

-- 4. 验证字段和索引是否成功添加
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_DEFAULT, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'verto_application' AND COLUMN_NAME = 'app_level';

SELECT INDEX_NAME, COLUMN_NAME 
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'verto_application' AND INDEX_NAME = 'idx_app_level';