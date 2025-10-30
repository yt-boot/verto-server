-- 员工积分流水表新增脚本（独立增量SQL）
-- 说明：请在 init.sql 之后执行本脚本，用于新增积分流水相关表结构。

USE `verto`;

-- 创建员工积分流水表（如果不存在）
CREATE TABLE IF NOT EXISTS `staff_points_log` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `event_type` varchar(100) COMMENT '事件类型',
  `source_type` varchar(20) COMMENT '来源类型(APP/PROJECT/COMPONENT/OTHER)',
  `source_id` varchar(64) COMMENT '来源ID',
  `source_name` varchar(200) COMMENT '来源名称',
  `delta` int NOT NULL DEFAULT '0' COMMENT '积分变动(支持负数)',
  `remark` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_staff_id` (`staff_id`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_source_type` (`source_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分流水表';

-- 可选：如果需要外键约束，可取消以下注释，并确保 staff.id 存在且类型匹配
-- ALTER TABLE `staff_points_log`
--   ADD CONSTRAINT `fk_staff_points_log_staff`
--   FOREIGN KEY (`staff_id`) REFERENCES `staff`(`id`);