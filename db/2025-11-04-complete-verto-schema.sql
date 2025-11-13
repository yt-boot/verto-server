-- Verto 完整数据库结构 SQL（基于最新《数据库表结构设计.md》与现有 SQL 脚本对齐）
-- 生成日期：2025-11-04
-- 说明：全部表名统一使用 verto_ 前缀；包含新建的关系/约束；去除用户计划删除的表。

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";
/*!40101 SET NAMES utf8mb4 */;
START TRANSACTION;

-- ==========================================
-- 基础人员表：verto_staff（来自原 staff 表）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_staff` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `employee_no` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工编号',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `hire_date` date DEFAULT NULL COMMENT '入职日期',
  `work_location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工作地点',
  `skills` text COLLATE utf8mb4_unicode_ci COMMENT '技能列表(JSON数组)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:离职,1:在职,2:休假)',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员管理表';

-- ==========================================
-- 应用管理：verto_application（来自原 app_manage 表，保留 extra_info）
-- managers 改为通过关系表维护（JSON字段如保留则视为兼容字段）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_application` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `app_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
  `app_description` text COLLATE utf8mb4_unicode_ci COMMENT '应用描述',
  `git_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git仓库地址',
  `domain` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用领域',
  `managers` text COLLATE utf8mb4_unicode_ci COMMENT '管理员列表(JSON数组，已由关系表替代)',
  `extra_info` longtext COLLATE utf8mb4_unicode_ci COMMENT '应用附加信息(JSON：技术栈、备注等)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_domain` (`domain`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用管理表';

-- 应用-管理员关系（多对多）：verto_application_staff_manager
CREATE TABLE IF NOT EXISTS `verto_application_staff_manager` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `application_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID（verto_application.id）',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID（verto_staff.id）',
  `role` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT 'manager' COMMENT '角色(manager/owner/maintainer)',
  `assigned_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
  `assigned_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_staff` (`application_id`,`staff_id`),
  KEY `idx_staff` (`staff_id`),
  CONSTRAINT `fk_app_mgr_application` FOREIGN KEY (`application_id`) REFERENCES `verto_application` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_app_mgr_staff` FOREIGN KEY (`staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用与管理员多对多关系';

-- ==========================================
-- 流水线定义：verto_pipeline（新增，应用：多对一）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_pipeline` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `application_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID（verto_application.id）',
  `pipeline_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水线名称',
  `pipeline_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水线类型(build:构建,deploy:部署,test:测试)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'enabled' COMMENT '状态(enabled/disabled)',
  `job_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流水线URL地址',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注描述',
  `config` text COLLATE utf8mb4_unicode_ci COMMENT '流水线配置(JSON)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_app_id` (`application_id`),
  KEY `idx_pipeline_type` (`pipeline_type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_pipeline_application` FOREIGN KEY (`application_id`) REFERENCES `verto_application` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用绑定的流水线定义表';

-- ==========================================
-- 项目管理：verto_project（来自原 project 表；开发者改为多对多关系表）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_project` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `project_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目类型(requirement:需求,bug:缺陷)',
  `requirement_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '需求ID',
  `bug_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缺陷ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `related_app_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联应用ID（verto_application.id）',
  `related_app_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联应用名称',
  `design_links` text COLLATE utf8mb4_unicode_ci COMMENT '设计链接(JSON数组)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `test_time` datetime DEFAULT NULL COMMENT '测试时间',
  `online_time` datetime DEFAULT NULL COMMENT '上线时间',
  `release_time` datetime DEFAULT NULL COMMENT '发布时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'planning' COMMENT '状态(planning:规划中,developing:开发中,testing:测试中,released:已发布)',
  `git_branch` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git分支',
  `app_config` text COLLATE utf8mb4_unicode_ci COMMENT '应用配置(JSON)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `priority` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT 'low' COMMENT '优先级',
  PRIMARY KEY (`id`),
  KEY `idx_project_type` (`project_type`),
  KEY `idx_requirement_id` (`requirement_id`),
  KEY `idx_bug_id` (`bug_id`),
  KEY `idx_status` (`status`),
  KEY `idx_project_priority` (`priority`),
  KEY `idx_related_app_id` (`related_app_id`),
  CONSTRAINT `fk_project_related_app` FOREIGN KEY (`related_app_id`) REFERENCES `verto_application` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目管理表';

-- 项目-人员关系（多对多）：verto_project_staff_relation
CREATE TABLE IF NOT EXISTS `verto_project_staff_relation` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `project_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目ID（verto_project.id）',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID（verto_staff.id）',
  `role` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色(dev/test/pm等)',
  `joined_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_staff` (`project_id`,`staff_id`),
  KEY `idx_staff` (`staff_id`),
  CONSTRAINT `fk_project_staff_project` FOREIGN KEY (`project_id`) REFERENCES `verto_project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_project_staff_staff` FOREIGN KEY (`staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目与人员多对多关系';

-- ==========================================
-- 项目流水线：verto_project_pipeline（引用 verto_pipeline）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_project_pipeline` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `project_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目ID',
  `pipeline_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水线定义ID（verto_pipeline.id）',
  `pipeline_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流水线名称（冗余）',
  `pipeline_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流水线类型（冗余）',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '状态(pending/running/success/failed)',
  `trigger_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发类型(manual/auto/schedule)',
  `trigger_user` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发用户',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT NULL COMMENT '执行时长(秒)',
  `build_number` int DEFAULT NULL COMMENT '构建编号',
  `git_commit` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git提交哈希',
  `git_branch` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git分支',
  `environment` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境(dev/test/prod)',
  `logs` longtext COLLATE utf8mb4_unicode_ci COMMENT '执行日志',
  `config` text COLLATE utf8mb4_unicode_ci COMMENT '流水线配置(JSON)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_pipeline_id` (`pipeline_id`),
  KEY `idx_pipeline_type` (`pipeline_type`),
  KEY `idx_status` (`status`),
  KEY `idx_build_number` (`build_number`),
  CONSTRAINT `fk_proj_pipeline_project` FOREIGN KEY (`project_id`) REFERENCES `verto_project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_proj_pipeline_pipeline` FOREIGN KEY (`pipeline_id`) REFERENCES `verto_pipeline` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目流水线执行记录表';

-- ==========================================
-- 物料模板：verto_material_template（来自原 material_template，author 关联 staff）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_material_template` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `template_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板类型(page:页面模板,component:组件模板,project:项目模板)',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '模板描述',
  `version` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '1.0.0' COMMENT '版本号',
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者名称（冗余）',
  `author_staff_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者员工ID（verto_staff.id）',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '标签(JSON数组)',
  `framework` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '技术框架',
  `preview_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览图片URL',
  `source_code` longtext COLLATE utf8mb4_unicode_ci COMMENT '源代码',
  `config_schema` text COLLATE utf8mb4_unicode_ci COMMENT '配置模式(JSON Schema)',
  `demo_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '演示地址',
  `documentation` text COLLATE utf8mb4_unicode_ci COMMENT '文档说明',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `star_count` int DEFAULT '0' COMMENT '收藏次数',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_author_staff_id` (`author_staff_id`),
  CONSTRAINT `fk_template_author_staff` FOREIGN KEY (`author_staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料模板表';

-- 应用-模板一对一关系：verto_application_template_relation
CREATE TABLE IF NOT EXISTS `verto_application_template_relation` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `application_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID（verto_application.id）',
  `template_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板ID（verto_material_template.id）',
  `bind_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_application_id` (`application_id`),
  UNIQUE KEY `uk_template_id` (`template_id`),
  UNIQUE KEY `uk_app_template_pair` (`application_id`,`template_id`),
  CONSTRAINT `fk_app_template_app` FOREIGN KEY (`application_id`) REFERENCES `verto_application` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_app_template_template` FOREIGN KEY (`template_id`) REFERENCES `verto_material_template` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用与物料模板一对一绑定关系';

-- ==========================================
-- OAuth 集成（来自原 oauth_* 表，统一前缀为 verto_）
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_oauth_token` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `access_token` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问令牌',
  `token_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT 'Bearer' COMMENT '令牌类型',
  `scope` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限范围',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间（如有）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方OAuth访问令牌表';

CREATE TABLE IF NOT EXISTS `verto_oauth_user` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `login` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方登录名（如 GitHub login）',
  `name` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '显示名称',
  `avatar_url` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像地址',
  `email` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `bound_at` datetime DEFAULT NULL COMMENT '绑定时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_token_id` bigint DEFAULT NULL COMMENT '最近一次关联的令牌ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_oauth_user` (`platform`,`oauth_user_id`),
  KEY `idx_login` (`login`),
  KEY `fk_oauth_user_last_token` (`last_token_id`),
  CONSTRAINT `fk_verto_oauth_user_last_token` FOREIGN KEY (`last_token_id`) REFERENCES `verto_oauth_token` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方OAuth用户绑定表';

CREATE TABLE IF NOT EXISTS `verto_oauth_binding` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `system_user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统用户ID',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_platform` (`system_user_id`,`platform`),
  KEY `idx_platform_oauth_user` (`platform`,`oauth_user_id`),
  CONSTRAINT `fk_verto_binding_oauth_user` FOREIGN KEY (`platform`, `oauth_user_id`) REFERENCES `verto_oauth_user` (`platform`, `oauth_user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户与第三方OAuth用户绑定关系表';

-- ==========================================
-- 积分系统：流水日志 + 扩展规则/余额/去重表
-- ==========================================
CREATE TABLE IF NOT EXISTS `verto_staff_points_log` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID（verto_staff.id）',
  `event_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型(APP/PROJECT/COMPONENT/OTHER)',
  `source_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源ID',
  `source_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源名称',
  `delta` int NOT NULL DEFAULT '0' COMMENT '积分变动(支持负数)',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_staff_id` (`staff_id`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_source_type` (`source_type`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_points_log_staff` FOREIGN KEY (`staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分流水表';

-- 积分规则：verto_staff_points_rule（示例设计，支持按事件/来源类型定义）
CREATE TABLE IF NOT EXISTS `verto_staff_points_rule` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `event_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件类型',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型',
  `points` int NOT NULL DEFAULT '0' COMMENT '基础积分',
  `max_points_per_day` int DEFAULT NULL COMMENT '每日上限',
  `status` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT 'enabled' COMMENT '状态(enabled/disabled)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_event_source` (`event_type`,`source_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分规则表';

-- 积分余额：verto_staff_points_balance（每人一条）
CREATE TABLE IF NOT EXISTS `verto_staff_points_balance` (
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID（verto_staff.id）',
  `balance` int NOT NULL DEFAULT '0' COMMENT '当前积分余额',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`staff_id`),
  CONSTRAINT `fk_points_balance_staff` FOREIGN KEY (`staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分余额表';

-- 积分去重：verto_staff_points_dedup（防重复发放；按事件+来源+员工唯一）
CREATE TABLE IF NOT EXISTS `verto_staff_points_dedup` (
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID',
  `event_type` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件类型',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型',
  `source_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源ID',
  `dedup_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '去重键（可为哈希）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dedup_key` (`dedup_key`),
  KEY `idx_staff_event_source` (`staff_id`,`event_type`,`source_type`),
  CONSTRAINT `fk_points_dedup_staff` FOREIGN KEY (`staff_id`) REFERENCES `verto_staff` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分去重索引表';

COMMIT;