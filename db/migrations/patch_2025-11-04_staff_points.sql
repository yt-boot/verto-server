-- 积分体系扩展：规则表、余额汇总表、幂等防重表
-- 说明：与已有 staff_points_log 表配合，实现事件驱动的自动积分发放
-- 数据库：MySQL 5.7（支持 JSON 类型）

-- 1) 员工积分规则表：配置事件→积分变动→限制条件（按天/首次/阈值等）
CREATE TABLE IF NOT EXISTS `staff_points_rule` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `event_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件编码(如: APP_CREATE, PROJECT_JOIN, PIPELINE_SUCCESS)',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型(APP/PROJECT/COMPONENT/OTHER)',
  `match_source_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '匹配的来源ID(为空表示所有来源)',
  `delta` int NOT NULL DEFAULT '0' COMMENT '积分变动(支持负数)',
  `limit_per_day` int DEFAULT NULL COMMENT '每日上限(同事件同人)',
  `first_time_only` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否仅首次触发有效',
  `condition_expr` json DEFAULT NULL COMMENT '条件表达式(JSON, 如字段匹配/阈值)',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用(0禁用/1启用)',
  `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '规则备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_source` (`event_code`,`source_type`),
  KEY `idx_match_source_id` (`match_source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分规则表';

-- 2) 员工积分余额汇总表：快速查询个人当前积分（避免每次聚合流水）
CREATE TABLE IF NOT EXISTS `staff_points_balance` (
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '当前总积分',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分余额汇总';

-- 3) 积分事件防重幂等表：同一事件唯一键防止重复加分
CREATE TABLE IF NOT EXISTS `staff_points_dedup` (
  `dedup_key` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '幂等键(如 staffId + eventCode + sourceId + 日期)',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID',
  `event_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件编码',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型(APP/PROJECT/COMPONENT/OTHER)',
  `source_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`dedup_key`),
  KEY `idx_staff_event` (`staff_id`,`event_code`),
  KEY `idx_source` (`source_type`,`source_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分事件防重幂等';

-- 可选：初始化示例规则（按需启用）
-- INSERT INTO staff_points_rule (id, event_code, source_type, delta, limit_per_day, first_time_only, enabled, remark)
-- VALUES
--   ('R001', 'APP_CREATE', 'APP', 50, 5, 0, 1, '创建应用奖励50分，每日最多5次'),
--   ('R002', 'PROJECT_CREATE', 'PROJECT', 30, NULL, 1, 1, '首次创建项目奖励30分，仅首次'),
--   ('R003', 'PIPELINE_SUCCESS', 'PROJECT', 10, 10, 0, 1, '流水线成功奖励10分，每日最多10次');