-- Verto Backend 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `verto` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `verto`;

-- 应用管理表
DROP TABLE IF EXISTS `app_manage`;
CREATE TABLE `app_manage` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `app_name` varchar(100) NOT NULL COMMENT '应用名称',
  `app_description` text COMMENT '应用描述',
  `git_url` varchar(500) COMMENT 'Git仓库地址',
  `domain` varchar(50) COMMENT '应用领域',
  `managers` text COMMENT '管理员列表(JSON数组)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_app_name` (`app_name`),
  KEY `idx_domain` (`domain`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用管理表';

-- 人员管理表
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `employee_no` varchar(20) NOT NULL COMMENT '员工编号',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `phone` varchar(20) COMMENT '手机号',
  `hire_date` date COMMENT '入职日期',
  `work_location` varchar(200) COMMENT '工作地点',
  `skills` text COMMENT '技能列表(JSON数组)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:离职,1:在职,2:休假)',
  `remark` text COMMENT '备注',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_no` (`employee_no`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人员管理表';

-- 项目管理表
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `project_type` varchar(20) NOT NULL COMMENT '项目类型(requirement:需求,bug:缺陷)',
  `requirement_id` varchar(50) COMMENT '需求ID',
  `bug_id` varchar(50) COMMENT '缺陷ID',
  `title` varchar(200) NOT NULL COMMENT '项目标题',
  `description` text COMMENT '项目描述',
  `related_app_id` varchar(32) COMMENT '关联应用ID',
  `related_app_name` varchar(100) COMMENT '关联应用名称',
  `developer_id` varchar(32) COMMENT '开发者ID',
  `developer_name` varchar(50) COMMENT '开发者姓名',
  `design_links` text COMMENT '设计链接(JSON数组)',
  `start_time` datetime COMMENT '开始时间',
  `test_time` datetime COMMENT '测试时间',
  `online_time` datetime COMMENT '上线时间',
  `release_time` datetime COMMENT '发布时间',
  `status` varchar(20) DEFAULT 'planning' COMMENT '状态(planning:规划中,developing:开发中,testing:测试中,released:已发布)',
  `git_branch` varchar(100) COMMENT 'Git分支',
  `app_config` text COMMENT '应用配置(JSON)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_type` (`project_type`),
  KEY `idx_requirement_id` (`requirement_id`),
  KEY `idx_bug_id` (`bug_id`),
  KEY `idx_status` (`status`),
  KEY `idx_developer_id` (`developer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目管理表';

-- 项目流水线表
DROP TABLE IF EXISTS `project_pipeline`;
CREATE TABLE `project_pipeline` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `project_id` varchar(32) NOT NULL COMMENT '项目ID',
  `pipeline_name` varchar(100) NOT NULL COMMENT '流水线名称',
  `pipeline_type` varchar(20) NOT NULL COMMENT '流水线类型(build:构建,deploy:部署,test:测试)',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态(pending:等待,running:运行中,success:成功,failed:失败)',
  `trigger_type` varchar(20) COMMENT '触发类型(manual:手动,auto:自动,schedule:定时)',
  `trigger_user` varchar(50) COMMENT '触发用户',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `duration` int COMMENT '执行时长(秒)',
  `build_number` int COMMENT '构建编号',
  `git_commit` varchar(40) COMMENT 'Git提交哈希',
  `git_branch` varchar(100) COMMENT 'Git分支',
  `environment` varchar(20) COMMENT '环境(dev:开发,test:测试,prod:生产)',
  `logs` longtext COMMENT '执行日志',
  `config` text COMMENT '流水线配置(JSON)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_pipeline_type` (`pipeline_type`),
  KEY `idx_status` (`status`),
  KEY `idx_build_number` (`build_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目流水线表';

-- 物料管理 - 组件表
DROP TABLE IF EXISTS `material_component`;
CREATE TABLE `material_component` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `component_name` varchar(100) NOT NULL COMMENT '组件名称',
  `component_code` varchar(50) NOT NULL COMMENT '组件编码',
  `component_type` varchar(20) NOT NULL COMMENT '组件类型(ui:UI组件,business:业务组件,tool:工具组件)',
  `category` varchar(50) COMMENT '分类',
  `description` text COMMENT '组件描述',
  `version` varchar(20) DEFAULT '1.0.0' COMMENT '版本号',
  `author` varchar(50) COMMENT '作者',
  `tags` text COMMENT '标签(JSON数组)',
  `dependencies` text COMMENT '依赖(JSON数组)',
  `props` text COMMENT '属性配置(JSON)',
  `demo_code` text COMMENT '示例代码',
  `documentation` text COMMENT '文档说明',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `star_count` int DEFAULT '0' COMMENT '收藏次数',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_component_code` (`component_code`),
  KEY `idx_component_name` (`component_name`),
  KEY `idx_component_type` (`component_type`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料组件表';

-- 物料管理 - 模板表
DROP TABLE IF EXISTS `material_template`;
CREATE TABLE `material_template` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `template_type` varchar(20) NOT NULL COMMENT '模板类型(page:页面模板,component:组件模板,project:项目模板)',
  `category` varchar(50) COMMENT '分类',
  `description` text COMMENT '模板描述',
  `version` varchar(20) DEFAULT '1.0.0' COMMENT '版本号',
  `author` varchar(50) COMMENT '作者',
  `tags` text COMMENT '标签(JSON数组)',
  `framework` varchar(50) COMMENT '技术框架',
  `preview_image` varchar(500) COMMENT '预览图片URL',
  `source_code` longtext COMMENT '源代码',
  `config_schema` text COMMENT '配置模式(JSON Schema)',
  `demo_url` varchar(500) COMMENT '演示地址',
  `documentation` text COMMENT '文档说明',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `star_count` int DEFAULT '0' COMMENT '收藏次数',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(0:禁用,1:启用)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_template_name` (`template_name`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_category` (`category`),
  KEY `idx_framework` (`framework`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物料模板表';

-- 插入应用管理测试数据
INSERT INTO `app_manage` (`id`, `app_name`, `app_description`, `git_url`, `domain`, `managers`, `status`, `create_by`, `update_by`) VALUES
('1', 'Verto平台', '企业级低代码开发平台，提供应用管理、项目管理、人员管理等功能', 'https://github.com/company/verto-platform.git', 'platform', '["admin", "manager1"]', 1, 'admin', 'admin'),
('2', '用户中心', '统一用户认证和权限管理系统，支持SSO单点登录', 'https://github.com/company/user-center.git', 'auth', '["manager2"]', 1, 'admin', 'manager2'),
('3', '数据分析平台', '企业数据可视化分析平台，支持多维度数据展示和报表生成', 'https://github.com/company/data-analytics.git', 'analytics', '["analyst1", "analyst2"]', 1, 'admin', 'analyst1');

-- 插入人员管理测试数据
INSERT INTO `staff` (`id`, `name`, `employee_no`, `email`, `phone`, `hire_date`, `work_location`, `skills`, `status`, `remark`, `create_by`, `update_by`) VALUES
('1', '张三', 'EMP001', 'zhangsan@company.com', '13800138001', '2023-01-15', 'A座3楼301工位', '["Java", "Spring Boot", "MySQL", "Vue.js"]', 1, '资深后端开发工程师，负责核心业务系统开发', 'admin', 'hr_manager'),
('2', '李四', 'EMP002', 'lisi@company.com', '13800138002', '2023-02-20', 'A座3楼302工位', '["JavaScript", "Vue.js", "React", "Node.js", "UI设计"]', 1, '前端开发工程师，擅长现代前端框架和用户体验设计', 'admin', 'hr_manager'),
('3', '王五', 'EMP003', 'wangwu@company.com', '13800138003', '2023-03-10', 'B座2楼201工位', '["Python", "Django", "PostgreSQL", "数据分析", "Docker"]', 1, 'Python开发工程师，专注于数据处理和分析系统', 'admin', 'tech_lead');

-- 插入项目管理测试数据
INSERT INTO `project` (`id`, `project_type`, `requirement_id`, `title`, `description`, `related_app_id`, `related_app_name`, `developer_id`, `developer_name`, `design_links`, `start_time`, `test_time`, `online_time`, `release_time`, `status`, `git_branch`, `create_by`, `update_by`) VALUES
('1', 'requirement', 'REQ-2024-001', '智能办公系统需求', '基于AI的智能办公管理系统需求开发', 'app_1_1', '智能办公前端', 'zhangsan', '张三', '[{"id":"design_1_1","title":"智能办公系统原型","url":"https://axure.com/smart-office-prototype","type":"prototype"}]', '2024-01-01 10:00:00', '2024-07-15 09:00:00', '2024-08-28 16:00:00', '2024-09-01 10:00:00', 'released', 'feature-REQ-2024-001', 'admin', 'admin'),
('2', 'requirement', 'REQ-2024-002', '电商平台升级需求', '电商平台技术架构升级改造需求', 'app_2_1', '电商前端', 'lisi', '李四', '[{"id":"design_2_1","title":"电商升级原型","url":"https://axure.com/ecommerce-upgrade-prototype","type":"prototype"}]', '2024-02-01 09:00:00', '2024-06-15 14:00:00', NULL, NULL, 'testing', 'feature-ecommerce-upgrade', 'admin', 'admin');

-- 插入项目流水线测试数据
INSERT INTO `project_pipeline` (`id`, `project_id`, `pipeline_name`, `pipeline_type`, `status`, `trigger_type`, `trigger_user`, `start_time`, `end_time`, `duration`, `build_number`, `git_commit`, `git_branch`, `environment`, `create_by`, `update_by`) VALUES
('1', '1', '智能办公系统构建', 'build', 'success', 'auto', 'zhangsan', '2024-01-15 10:00:00', '2024-01-15 10:15:00', 900, 1, 'a1b2c3d4e5f6', 'feature-REQ-2024-001', 'dev', 'admin', 'admin'),
('2', '1', '智能办公系统部署', 'deploy', 'success', 'manual', 'zhangsan', '2024-01-15 10:20:00', '2024-01-15 10:25:00', 300, 1, 'a1b2c3d4e5f6', 'feature-REQ-2024-001', 'prod', 'admin', 'admin'),
('3', '2', '电商平台构建', 'build', 'running', 'auto', 'lisi', '2024-02-15 14:00:00', NULL, NULL, 2, 'b2c3d4e5f6g7', 'feature-ecommerce-upgrade', 'test', 'admin', 'admin');

-- 插入物料组件测试数据
INSERT INTO `material_component` (`id`, `component_name`, `component_code`, `component_type`, `category`, `description`, `version`, `author`, `tags`, `dependencies`, `props`, `demo_code`, `documentation`, `download_count`, `star_count`, `create_by`, `update_by`) VALUES
('1', '数据表格组件', 'DataTable', 'ui', '表格', '支持分页、排序、筛选的高性能数据表格组件', '2.1.0', 'zhangsan', '["表格", "分页", "排序"]', '["vue", "ant-design-vue"]', '{"columns": "array", "dataSource": "array", "pagination": "object"}', '<DataTable :columns="columns" :dataSource="data" />', '功能强大的数据表格组件，支持多种交互功能', 1250, 89, 'admin', 'zhangsan'),
('2', '表单构建器', 'FormBuilder', 'business', '表单', '可视化表单构建器，支持拖拽生成表单', '1.5.2', 'lisi', '["表单", "拖拽", "可视化"]', '["vue", "element-plus"]', '{"schema": "object", "model": "object"}', '<FormBuilder :schema="formSchema" v-model="formData" />', '强大的表单构建工具，支持多种表单控件', 890, 67, 'admin', 'lisi');

-- 插入物料模板测试数据
INSERT INTO `material_template` (`id`, `template_name`, `template_code`, `template_type`, `category`, `description`, `version`, `author`, `tags`, `framework`, `preview_image`, `source_code`, `config_schema`, `demo_url`, `documentation`, `download_count`, `star_count`, `create_by`, `update_by`) VALUES
('1', '管理后台模板', 'AdminTemplate', 'project', '后台管理', '基于Vue3+Vite的现代化管理后台模板', '3.2.1', 'wangwu', '["Vue3", "Vite", "管理后台"]', 'Vue3', 'https://example.com/preview/admin-template.png', '// Vue3 Admin Template Source Code', '{"theme": "string", "layout": "string"}', 'https://demo.admin-template.com', '完整的管理后台解决方案', 2340, 156, 'admin', 'wangwu'),
('2', '移动端H5模板', 'MobileH5Template', 'project', '移动端', '响应式移动端H5应用模板', '2.0.8', 'zhaoliu', '["H5", "移动端", "响应式"]', 'Vue3', 'https://example.com/preview/mobile-h5.png', '// Mobile H5 Template Source Code', '{"theme": "string", "features": "array"}', 'https://demo.mobile-h5.com', '适配各种移动设备的H5模板', 1680, 92, 'admin', 'zhaoliu');

-- 配置管理表
DROP TABLE IF EXISTS `app_config`;
CREATE TABLE `app_config` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '配置名称',
  `type` varchar(50) NOT NULL COMMENT '配置类型(pipeline/tracking/code-review/other)',
  `status` varchar(20) DEFAULT 'disabled' COMMENT '状态(enabled/disabled)',
  `environment` varchar(20) COMMENT '环境(dev/test/prod)',
  `description` text COMMENT '描述',
  `app_id` varchar(32) COMMENT '关联应用ID',
  `config` longtext COMMENT '配置内容(JSON)',
  `create_by` varchar(50) COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(50) COMMENT '更新人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_env` (`environment`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配置管理表';