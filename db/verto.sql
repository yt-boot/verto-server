-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- 主机： mysql:3306
-- 生成日期： 2025-10-30 02:07:32
-- 服务器版本： 9.4.0
-- PHP 版本： 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `verto`
--

-- --------------------------------------------------------

--
-- 表的结构 `app_config`
--

CREATE TABLE `app_config` (
  `id` varchar(64) NOT NULL,
  `name` varchar(128) NOT NULL,
  `type` varchar(32) NOT NULL,
  `status` varchar(16) NOT NULL,
  `environment` varchar(32) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `app_id` varchar(64) DEFAULT NULL,
  `config` text,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 表的结构 `app_config_content`
--

CREATE TABLE `app_config_content` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `config_id` varchar(64) NOT NULL COMMENT '关联配置ID（app_config.id）',
  `type` varchar(32) NOT NULL COMMENT '配置类型(pipeline/tracking/code_review)',
  `content` longtext COMMENT '配置内容（完整JSON字符串）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='配置内容详情表（按类型存储完整JSON内容）';

-- --------------------------------------------------------

--
-- 表的结构 `app_config_relation`
--

CREATE TABLE `app_config_relation` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `config_id` varchar(64) NOT NULL COMMENT '关联配置ID（app_config.id）',
  `ref_type` varchar(32) NOT NULL COMMENT '关联类型(pipeline_stage/tracking_event/tracking_property/code_review_rule/code_reviewer)',
  `ref_id` varchar(128) DEFAULT NULL COMMENT '子项ID（如阶段ID/事件ID/规则ID等）',
  `ref_name` varchar(128) DEFAULT NULL COMMENT '子项名称（展示名称）',
  `extra` longtext COMMENT '额外信息（JSON字符串，存子项详细属性）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='配置与子项的关联索引表';

-- --------------------------------------------------------

--
-- 表的结构 `app_git_repo_info`
--

CREATE TABLE `app_git_repo_info` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `app_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `owner` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库所有者',
  `repo_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库名称',
  `html_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '仓库页面地址',
  `clone_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'HTTP克隆地址',
  `ssh_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SSH克隆地址',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '仓库描述',
  `visibility` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可见性(public/private)',
  `stars` int DEFAULT '0' COMMENT 'Star数量',
  `forks` int DEFAULT '0' COMMENT 'Fork数量',
  `open_issues` int DEFAULT '0' COMMENT '未关闭Issue数量',
  `license` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '许可证',
  `topics` text COLLATE utf8mb4_unicode_ci COMMENT '主题标签(逗号分隔)',
  `default_branch` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '默认分支',
  `branch_count` int DEFAULT '0' COMMENT '分支数量',
  `last_commit_sha` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后一次提交SHA',
  `last_commit_message` text COLLATE utf8mb4_unicode_ci COMMENT '最后一次提交信息',
  `last_committer` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后提交者',
  `last_commit_time` datetime DEFAULT NULL COMMENT '最后提交时间',
  `created_at` datetime DEFAULT NULL COMMENT '仓库创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '仓库更新时间',
  `last_synced_at` datetime DEFAULT NULL COMMENT '最近同步时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用Git仓库信息表';

-- --------------------------------------------------------

--
-- 表的结构 `app_manage`
--

CREATE TABLE `app_manage` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `app_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用名称',
  `app_description` text COLLATE utf8mb4_unicode_ci COMMENT '应用描述',
  `git_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git仓库地址',
  `domain` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用领域',
  `managers` text COLLATE utf8mb4_unicode_ci COMMENT '管理员列表(JSON数组)',
  `extra_info` longtext COLLATE utf8mb4_unicode_ci COMMENT '应用附加信息(JSON：技术栈、备注等)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用管理表';

--
-- 转存表中的数据 `app_manage`
--

INSERT INTO `app_manage` (`id`, `app_name`, `app_description`, `git_url`, `domain`, `managers`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1', 'Verto平台', '企业级低代码开发平台，提供应用管理、项目管理、人员管理等功能', 'https://github.com/company/verto-platform.git', 'platform', '[\"admin\", \"manager1\"]', 1, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23'),
('1980812752697331714', 'jeecg-boot', '一个问题', 'https://github.com/wangyuanzcm/auto-script.git', 'a-001', '1', 1, 'admin', '2025-10-22 09:45:37', NULL, '2025-10-22 01:45:36'),
('2', '用户中心', '统一用户认证和权限管理系统，支持SSO单点登录', 'https://github.com/company/user-center.git', 'auth', '[\"manager2\"]', 1, 'admin', '2025-10-21 07:22:23', 'manager2', '2025-10-21 07:22:23'),
('3', '数据分析平台', '企业数据可视化分析平台，支持多维度数据展示和报表生成', 'https://github.com/company/data-analytics.git', 'analytics', '[\"analyst1\", \"analyst2\"]', 1, 'admin', '2025-10-21 07:22:23', 'analyst1', '2025-10-21 07:22:23');

-- --------------------------------------------------------

--
-- 表的结构 `app_pipeline_binding`
--

CREATE TABLE `app_pipeline_binding` (
  `id` varchar(64) NOT NULL,
  `app_id` varchar(64) NOT NULL,
  `environment` varchar(32) NOT NULL,
  `job_name` varchar(128) NOT NULL,
  `job_url` varchar(256) DEFAULT NULL,
  `status` varchar(16) NOT NULL DEFAULT 'enabled',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 表的结构 `material_component`
--

CREATE TABLE `material_component` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `component_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件名称',
  `component_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件编码',
  `component_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件类型(ui:UI组件,business:业务组件,tool:工具组件)',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '组件描述',
  `version` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '1.0.0' COMMENT '版本号',
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `tags` text COLLATE utf8mb4_unicode_ci COMMENT '标签(JSON数组)',
  `dependencies` text COLLATE utf8mb4_unicode_ci COMMENT '依赖(JSON数组)',
  `props` text COLLATE utf8mb4_unicode_ci COMMENT '属性配置(JSON)',
  `demo_code` text COLLATE utf8mb4_unicode_ci COMMENT '示例代码',
  `documentation` text COLLATE utf8mb4_unicode_ci COMMENT '文档说明',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `star_count` int DEFAULT '0' COMMENT '收藏次数',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料组件表';

--
-- 转存表中的数据 `material_component`
--

INSERT INTO `material_component` (`id`, `component_name`, `component_code`, `component_type`, `category`, `description`, `version`, `author`, `tags`, `dependencies`, `props`, `demo_code`, `documentation`, `download_count`, `star_count`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1', '数据表格组件', 'DataTable', 'ui', '表格', '支持分页、排序、筛选的高性能数据表格组件', '2.1.0', 'zhangsan', '[\"表格\", \"分页\", \"排序\"]', '[\"vue\", \"ant-design-vue\"]', '{\"columns\": \"array\", \"dataSource\": \"array\", \"pagination\": \"object\"}', '<DataTable :columns=\"columns\" :dataSource=\"data\" />', '功能强大的数据表格组件，支持多种交互功能', 1250, 89, 1, 'admin', '2025-10-21 07:22:23', 'zhangsan', '2025-10-21 07:22:23'),
('2', '表单构建器', 'FormBuilder', 'business', '表单', '可视化表单构建器，支持拖拽生成表单', '1.5.2', 'lisi', '[\"表单\", \"拖拽\", \"可视化\"]', '[\"vue\", \"element-plus\"]', '{\"schema\": \"object\", \"model\": \"object\"}', '<FormBuilder :schema=\"formSchema\" v-model=\"formData\" />', '强大的表单构建工具，支持多种表单控件', 890, 67, 1, 'admin', '2025-10-21 07:22:23', 'lisi', '2025-10-21 07:22:23');

-- --------------------------------------------------------

--
-- 表的结构 `material_template`
--

CREATE TABLE `material_template` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `template_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板类型(page:页面模板,component:组件模板,project:项目模板)',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '模板描述',
  `version` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '1.0.0' COMMENT '版本号',
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
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
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料模板表';

--
-- 转存表中的数据 `material_template`
--

INSERT INTO `material_template` (`id`, `template_name`, `template_code`, `template_type`, `category`, `description`, `version`, `author`, `tags`, `framework`, `preview_image`, `source_code`, `config_schema`, `demo_url`, `documentation`, `download_count`, `star_count`, `status`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1', '管理后台模板', 'AdminTemplate', 'project', '后台管理', '基于Vue3+Vite的现代化管理后台模板', '3.2.1', 'wangwu', '[\"Vue3\", \"Vite\", \"管理后台\"]', 'Vue3', 'https://example.com/preview/admin-template.png', '// Vue3 Admin Template Source Code', '{\"theme\": \"string\", \"layout\": \"string\"}', 'https://demo.admin-template.com', '完整的管理后台解决方案', 2340, 156, 1, 'admin', '2025-10-21 07:22:23', 'wangwu', '2025-10-21 07:22:23'),
('2', '移动端H5模板', 'MobileH5Template', 'project', '移动端', '响应式移动端H5应用模板', '2.0.8', 'zhaoliu', '[\"H5\", \"移动端\", \"响应式\"]', 'Vue3', 'https://example.com/preview/mobile-h5.png', '// Mobile H5 Template Source Code', '{\"theme\": \"string\", \"features\": \"array\"}', 'https://demo.mobile-h5.com', '适配各种移动设备的H5模板', 1680, 92, 1, 'admin', '2025-10-21 07:22:23', 'zhaoliu', '2025-10-21 07:22:23');

-- --------------------------------------------------------

--
-- 表的结构 `oauth_token`
--

CREATE TABLE `oauth_token` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `access_token` varchar(1024) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问令牌',
  `token_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT 'Bearer' COMMENT '令牌类型',
  `scope` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限范围',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expires_at` datetime DEFAULT NULL COMMENT '过期时间（如有）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方OAuth访问令牌表';

-- --------------------------------------------------------

--
-- 表的结构 `oauth_user`
--

CREATE TABLE `oauth_user` (
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
  `last_token_id` bigint DEFAULT NULL COMMENT '最近一次关联的令牌ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='第三方OAuth用户绑定表';

-- --------------------------------------------------------

--
-- 表的结构 `oauth_binding`
--

CREATE TABLE `oauth_binding` (
  `id` bigint NOT NULL COMMENT '主键ID（雪花算法生成）',
  `system_user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统用户ID',
  `platform` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识（如：github、gitlab）',
  `oauth_user_id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '第三方用户唯一ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户与第三方OAuth用户绑定关系表';

-- --------------------------------------------------------

--
-- 表的结构 `project`
--

CREATE TABLE `project` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `project_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目类型(requirement:需求,bug:缺陷)',
  `requirement_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '需求ID',
  `bug_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缺陷ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `related_app_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联应用ID',
  `related_app_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联应用名称',
  `developer_id` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开发者ID',
  `developer_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开发者姓名',
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
  `priority` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT 'low'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目管理表';

--
-- 转存表中的数据 `project`
--

INSERT INTO `project` (`id`, `project_type`, `requirement_id`, `bug_id`, `title`, `description`, `related_app_id`, `related_app_name`, `developer_id`, `developer_name`, `design_links`, `start_time`, `test_time`, `online_time`, `release_time`, `status`, `git_branch`, `app_config`, `create_by`, `create_time`, `update_by`, `update_time`, `priority`) VALUES
('1', 'requirement', 'REQ-2024-001', NULL, '智能办公系统需求', '基于AI的智能办公管理系统需求开发', 'app_1_1', '智能办公前端', 'zhangsan', '张三', '[{\"id\":\"design_1_1\",\"title\":\"智能办公系统原型\",\"url\":\"https://axure.com/smart-office-prototype\",\"type\":\"prototype\"}]', '2024-01-01 10:00:00', '2024-07-15 09:00:00', '2024-08-28 16:00:00', '2024-09-01 10:00:00', 'released', 'feature-REQ-2024-001', NULL, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23', NULL),
('2', 'requirement', 'REQ-2024-002', NULL, '电商平台升级需求', '电商平台技术架构升级改造需求', 'app_2_1', '电商前端', 'lisi', '李四', '[{\"id\":\"design_2_1\",\"title\":\"电商升级原型\",\"url\":\"https://axure.com/ecommerce-upgrade-prototype\",\"type\":\"prototype\"}]', '2024-02-01 09:00:00', '2024-06-15 14:00:00', NULL, NULL, 'testing', 'feature-ecommerce-upgrade', NULL, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23', NULL);

-- --------------------------------------------------------

--
-- 表的结构 `project_pipeline`
--

CREATE TABLE `project_pipeline` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `project_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目ID',
  `pipeline_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水线名称',
  `pipeline_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流水线类型(build:构建,deploy:部署,test:测试)',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'pending' COMMENT '状态(pending:等待,running:运行中,success:成功,failed:失败)',
  `trigger_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发类型(manual:手动,auto:自动,schedule:定时)',
  `trigger_user` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '触发用户',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `duration` int DEFAULT NULL COMMENT '执行时长(秒)',
  `build_number` int DEFAULT NULL COMMENT '构建编号',
  `git_commit` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git提交哈希',
  `git_branch` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Git分支',
  `environment` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '环境(dev:开发,test:测试,prod:生产)',
  `logs` longtext COLLATE utf8mb4_unicode_ci COMMENT '执行日志',
  `config` text COLLATE utf8mb4_unicode_ci COMMENT '流水线配置(JSON)',
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目流水线表';

--
-- 转存表中的数据 `project_pipeline`
--

INSERT INTO `project_pipeline` (`id`, `project_id`, `pipeline_name`, `pipeline_type`, `status`, `trigger_type`, `trigger_user`, `start_time`, `end_time`, `duration`, `build_number`, `git_commit`, `git_branch`, `environment`, `logs`, `config`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1', '1', '智能办公系统构建', 'build', 'success', 'auto', 'zhangsan', '2024-01-15 10:00:00', '2024-01-15 10:15:00', 900, 1, 'a1b2c3d4e5f6', 'feature-REQ-2024-001', 'dev', NULL, NULL, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23'),
('2', '1', '智能办公系统部署', 'deploy', 'success', 'manual', 'zhangsan', '2024-01-15 10:20:00', '2024-01-15 10:25:00', 300, 1, 'a1b2c3d4e5f6', 'feature-REQ-2024-001', 'prod', NULL, NULL, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23'),
('3', '2', '电商平台构建', 'build', 'running', 'auto', 'lisi', '2024-02-15 14:00:00', NULL, NULL, 2, 'b2c3d4e5f6g7', 'feature-ecommerce-upgrade', 'test', NULL, NULL, 'admin', '2025-10-21 07:22:23', 'admin', '2025-10-21 07:22:23');

-- --------------------------------------------------------

--
-- 表的结构 `staff`
--

CREATE TABLE `staff` (
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
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员管理表';

--
-- 转存表中的数据 `staff`
--

INSERT INTO `staff` (`id`, `name`, `employee_no`, `email`, `phone`, `hire_date`, `work_location`, `skills`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES
('1', '张三', 'EMP001', 'zhangsan@company.com', '13800138001', '2023-01-15', 'A座3楼301工位', '[\"Java\", \"Spring Boot\", \"MySQL\", \"Vue.js\"]', 1, '资深后端开发工程师，负责核心业务系统开发', 'admin', '2025-10-21 07:22:23', 'hr_manager', '2025-10-21 07:22:23'),
('2', '李四', 'EMP002', 'lisi@company.com', '13800138002', '2023-02-20', 'A座3楼302工位', '[\"JavaScript\", \"Vue.js\", \"React\", \"Node.js\", \"UI设计\"]', 1, '前端开发工程师，擅长现代前端框架和用户体验设计', 'admin', '2025-10-21 07:22:23', 'hr_manager', '2025-10-21 07:22:23'),
('3', '王五', 'EMP003', 'wangwu@company.com', '13800138003', '2023-03-10', 'B座2楼201工位', '[\"Python\", \"Django\", \"PostgreSQL\", \"数据分析\", \"Docker\"]', 1, 'Python开发工程师，专注于数据处理和分析系统', 'admin', '2025-10-21 07:22:23', 'tech_lead', '2025-10-21 07:22:23');

-- --------------------------------------------------------

--
-- 表的结构 `staff_points_log`
--

CREATE TABLE `staff_points_log` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键ID',
  `staff_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '员工ID',
  `event_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件类型',
  `source_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源类型(APP/PROJECT/COMPONENT/OTHER)',
  `source_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源ID',
  `source_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源名称',
  `delta` int NOT NULL DEFAULT '0' COMMENT '积分变动(支持负数)',
  `remark` text COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工积分流水表';

--
-- 转存表中的数据 `staff_points_log`
--

INSERT INTO `staff_points_log` (`id`, `staff_id`, `event_type`, `source_type`, `source_id`, `source_name`, `delta`, `remark`, `create_time`) VALUES
('1981181152665739266', '1', 'MANUAL_ADJUST', 'OTHER', NULL, NULL, 1, '优秀文化案例', '2025-10-23 10:09:30'),
('1981187079544422402', '1', 'MANUAL_ADJUST', 'APP', NULL, NULL, 1, '科室优秀文化案例', '2025-10-23 10:33:03');

--
-- 转储表的索引
--

--
-- 表的索引 `app_config`
--
ALTER TABLE `app_config`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_app_id` (`app_id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_update_time` (`update_time`);

--
-- 表的索引 `app_config_content`
--
ALTER TABLE `app_config_content`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uniq_config_id` (`config_id`),
  ADD KEY `idx_type` (`type`),
  ADD KEY `idx_update_time` (`update_time`);

--
-- 表的索引 `app_config_relation`
--
ALTER TABLE `app_config_relation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_config_type` (`config_id`,`ref_type`),
  ADD KEY `idx_ref_id` (`ref_id`),
  ADD KEY `idx_ref_name` (`ref_name`),
  ADD KEY `idx_update_time` (`update_time`);

--
-- 表的索引 `app_git_repo_info`
--
ALTER TABLE `app_git_repo_info`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_app_id` (`app_id`),
  ADD KEY `idx_owner_repo` (`owner`,`repo_name`),
  ADD KEY `idx_updated_at` (`updated_at`);

--
-- 表的索引 `app_manage`
--
ALTER TABLE `app_manage`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_app_name` (`app_name`),
  ADD KEY `idx_domain` (`domain`),
  ADD KEY `idx_status` (`status`);

--
-- 表的索引 `app_pipeline_binding`
--
ALTER TABLE `app_pipeline_binding`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_app_id` (`app_id`),
  ADD KEY `idx_environment` (`environment`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_update_time` (`update_time`),
  ADD KEY `idx_app_env` (`app_id`,`environment`);

--
-- 表的索引 `material_component`
--
ALTER TABLE `material_component`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_component_code` (`component_code`),
  ADD KEY `idx_component_name` (`component_name`),
  ADD KEY `idx_component_type` (`component_type`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_status` (`status`);

--
-- 表的索引 `material_template`
--
ALTER TABLE `material_template`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_template_code` (`template_code`),
  ADD KEY `idx_template_name` (`template_name`),
  ADD KEY `idx_template_type` (`template_type`),
  ADD KEY `idx_category` (`category`),
  ADD KEY `idx_framework` (`framework`),
  ADD KEY `idx_status` (`status`);

--
-- 表的索引 `oauth_token`
--
ALTER TABLE `oauth_token`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_platform_user` (`platform`,`oauth_user_id`);

--
-- 表的索引 `oauth_user`
--
ALTER TABLE `oauth_user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_platform_oauth_user` (`platform`,`oauth_user_id`),
  ADD KEY `idx_login` (`login`),
  ADD KEY `fk_oauth_user_last_token` (`last_token_id`);

--
-- 表的索引 `oauth_binding`
--
ALTER TABLE `oauth_binding`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_user_platform` (`system_user_id`,`platform`),
  ADD KEY `idx_platform_oauth_user` (`platform`,`oauth_user_id`);

--
-- 表的索引 `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_project_type` (`project_type`),
  ADD KEY `idx_requirement_id` (`requirement_id`),
  ADD KEY `idx_bug_id` (`bug_id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_developer_id` (`developer_id`),
  ADD KEY `idx_project_priority` (`priority`);

--
-- 表的索引 `project_pipeline`
--
ALTER TABLE `project_pipeline`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_project_id` (`project_id`),
  ADD KEY `idx_pipeline_type` (`pipeline_type`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_build_number` (`build_number`);

--
-- 表的索引 `staff`
--
ALTER TABLE `staff`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_employee_no` (`employee_no`),
  ADD UNIQUE KEY `uk_email` (`email`),
  ADD KEY `idx_name` (`name`),
  ADD KEY `idx_status` (`status`);

--
-- 表的索引 `staff_points_log`
--
ALTER TABLE `staff_points_log`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_staff_id` (`staff_id`),
  ADD KEY `idx_event_type` (`event_type`),
  ADD KEY `idx_source_type` (`source_type`),
  ADD KEY `idx_create_time` (`create_time`);

--
-- 限制导出的表
--

--
-- 限制表 `app_config_content`
--
ALTER TABLE `app_config_content`
  ADD CONSTRAINT `fk_acc_config` FOREIGN KEY (`config_id`) REFERENCES `app_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `app_config_relation`
--
ALTER TABLE `app_config_relation`
  ADD CONSTRAINT `fk_acr_config` FOREIGN KEY (`config_id`) REFERENCES `app_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `oauth_user`
--
ALTER TABLE `oauth_user`
  ADD CONSTRAINT `fk_oauth_user_last_token` FOREIGN KEY (`last_token_id`) REFERENCES `oauth_token` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT;

--
-- 限制表 `oauth_binding`
--
ALTER TABLE `oauth_binding`
  ADD CONSTRAINT `fk_binding_oauth_user` FOREIGN KEY (`platform`, `oauth_user_id`) REFERENCES `oauth_user` (`platform`, `oauth_user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
