# Verto Web 前端接口文档（正式稿）

> 文档位置：`verto-server/docs/api-doc.verto-web-apis.md`  
> 最后更新：2025-11-10

## 1. 文档范围与约定

- 覆盖 `verto-web/src/views` 下 Project、AppManage、Staff 三大模块的已实现与疑似未引用接口。
- 前端通过 `defHttp` 调用接口，部分接口设置 `isTransformResponse=false`，表示返回不经过统一转换；返回结构以实际后端实现为准。
- 路径前缀通常为 `/verto/`，其后按模块分组：`project`、`appmanage`、`staff` 等。
- 示例方法与路径与后端控制器风格保持一致（例如：`@DeleteMapping("/deleteBatch")` 对应 `DELETE /verto/appmanage/app/deleteBatch`）。

## 2. 模块概览

- Project：项目基础、Git 分支与提交、应用配置、流水线配置/状态/历史、构建与阶段操作、日志、部署、关联数据。
- AppManage：应用管理（CRUD）、流水线配置与历史、应用-流水线绑定、Git 仓库相关、统计与包信息、在职人员列表。
- Staff：人员管理（CRUD）、重复校验、技能/部门字典与统计、积分管理（汇总、流水、调整）。

---

## 3. Project 模块接口（`src/views/project/Project.api.ts`）

### 3.1 项目基础
- GET `/verto/project/list` — 查询项目列表
  - 入参：分页/筛选参数
  - 返回：项目列表

- POST `/verto/project/add` — 新增项目；或 PUT `/verto/project/edit` — 更新项目（`saveProject` 根据 `isUpdate` 决定）
  - 入参：`ProjectModel`；提交前对 `designLinks`、`appConfig` 序列化 JSON 字符串
  - 返回：保存/更新结果

- PUT `/verto/project/edit` — 更新项目（`updateProject`）
  - 入参：`ProjectModel`

- DELETE `/verto/project/delete` — 删除单个项目（`joinParamsToUrl: true`）
  - 入参：`id`

- DELETE `/verto/project/deleteBatch` — 批量删除项目（`joinParamsToUrl: true`）
  - 入参：`ids: string[]`

- GET `/verto/project/queryById` — 获取项目详情
  - 入参：`id`
  - 返回：`ProjectModel`

- POST `/verto/project/importExcel` — 导入项目数据
- GET `/verto/project/exportXls` — 导出项目数据（返回 Blob）

### 3.2 Git 分支/提交
- POST `/verto/project/git/createBranch` — 创建 Git 分支
  - 入参：`{ projectId, projectType: 'requirement' | 'bug', itemId, appId }`
  - 返回：`{ branchName, command, success, message }`

- GET `/verto/project/git/branches` — 获取分支列表
  - 入参：`projectId`

- GET `/verto/project/git/commits` — 获取提交列表
  - 入参：`{ projectId, branch?, page?, pageSize? }`

- DELETE `/verto/project/git/deleteBranch` — 删除分支
  - 入参：`{ projectId, branchName }`

### 3.3 应用配置
- GET `/verto/project/appConfig/get` — 获取应用配置
  - 入参：`projectId`
  - 返回：`AppConfig`

- POST `/verto/project/appConfig/save` — 保存应用配置
  - 入参：`{ projectId, config: AppConfig }`

### 3.4 流水线配置
- GET `/verto/project/pipeline/config/get` — 获取流水线配置
  - 入参：`projectId`
- PUT `/verto/project/pipeline/config/get/{projectId}` — 更新流水线配置（`data: config`）
- POST `/verto/project/pipeline/config/save` — 保存流水线配置
- POST `/verto/project/pipeline/config/toggle` — 启用/禁用流水线
  - 入参：`{ projectId, enabled }`

### 3.5 流水线状态
- GET `/verto/project/pipeline/status` — 获取流水线当前状态
  - 入参：`projectId`
  - 返回：`PipelineStatus`

### 3.6 流水线历史（构建记录）
- GET `/verto/project/pipeline/history` — 获取历史记录
  - 入参：`{ projectId, page?, pageSize?, status?, branch?, startDate?, endDate? }`

- GET `/verto/project/pipeline/build/{projectId}/{buildId}` — 构建详情
- DELETE `/verto/project/pipeline/build/delete/{projectId}/{buildId}` — 删除构建
- DELETE `/verto/project/pipeline/build/batch-delete/{projectId}` — 批量删除构建（`data: { buildIds }`）

### 3.7 流水线执行
- POST `/verto/project/pipeline/trigger` — 触发流水线
  - 入参：`{ projectId, environment, branch?, commitId?, parameters?, jobName?, bindingId? }`

- POST `/verto/project/pipeline/cancel/{projectId}/{buildId}` — 取消构建
- POST `/verto/project/pipeline/build/retry/{projectId}/{buildId}` — 重试构建

### 3.8 流水线阶段操作（待复核接入）
- POST `/verto/project/pipeline/stage/continue/{projectId}/{buildId}/{stageName}` — 继续阶段
- POST `/verto/project/pipeline/retry/{projectId}/{buildId}/{stageName}` — 重试阶段
- POST `/verto/project/pipeline/skip/{projectId}/{buildId}/{stageName}` — 跳过阶段
- POST `/verto/project/pipeline/stage/cancel/{projectId}/{buildId}/{stageName}` — 取消阶段
- GET `/verto/project/pipeline/logs/{projectId}/{buildId}/{stageName}` — 阶段日志（`offset?`, `limit?`, `follow?`）

> 说明：`PipelineFlowChart.vue` 与 `BuildDetailModal.vue` 仅 emit 相应事件，暂未发现父组件实际调用这些接口。

### 3.9 日志
- GET `/verto/project/pipeline/logs/{projectId}/{buildId}` — 构建日志（`offset?`, `limit?`, `follow?`）
- GET `/verto/project/pipeline/build/logs/download/{projectId}/{buildId}` — 构建日志下载（Blob）
- POST `/verto/project/pipeline/build/batch-download/{projectId}` — 批量日志下载（`data: { buildIds }`，返回 Blob）

### 3.10 部署（待复核接入）
- POST `/verto/project/pipeline/deploy` — 部署流水线
- POST `/verto/project/pipeline/stop` — 停止部署

### 3.11 关联数据
- GET `/verto/appmanage/app/list` — 应用列表（项目内选择）
- GET `/verto/staff/list` — 人员列表（项目内选择）
- GET `/verto/appmanage/pipeline/binding/list` — 流水线绑定列表（项目内选择/展示）

---

## 4. AppManage 模块接口（`src/views/appmanage/AppManage.api.ts`）

### 4.1 应用管理（CRUD）
- GET `/verto/appmanage/app/list` — 应用列表
- GET `/verto/appmanage/app/queryById` — 应用详情
- POST `/verto/appmanage/app/add` — 新增应用
- PUT `/verto/appmanage/app/edit` — 编辑应用
- DELETE `/verto/appmanage/app/delete` — 删除应用
- DELETE `/verto/appmanage/app/deleteBatch` — 批量删除（后端：`@DeleteMapping("/deleteBatch")`）

- GET `/sys/user/list` — 用户列表（选择负责人/成员）
- GET `/sys/dict/getDictItems/app_domain` — 域字典

### 4.2 流水线配置与历史
- GET `/verto/appmanage/pipeline/config` — 获取配置
- POST `/verto/project/pipeline/config/save` — 保存配置
- DELETE `/verto/appmanage/pipeline/config/delete` — 删除配置
- POST `/verto/appmanage/pipeline/config/toggle` — 切换启用
- POST `/verto/appmanage/pipeline/config/copy` — 复制配置

- GET `/verto/appmanage/pipeline/history` — 历史列表
- GET `/verto/appmanage/pipeline/history/detail` — 历史详情
- POST `/verto/appmanage/pipeline/rerun` — 重跑
- POST `/verto/appmanage/pipeline/cancel` — 取消
- GET `/verto/appmanage/pipeline/logs` — 日志

- POST `/verto/project/pipeline/jenkins/create` — 创建 Jenkins 流水线
- GET `/verto/appmanage/app/package-json` — 读取 `package.json`
- GET `/verto/appmanage/app/statistics` — 应用统计信息

### 4.3 应用-流水线绑定
- GET `/verto/appmanage/pipeline/binding/list` — 绑定列表
- POST `/verto/appmanage/pipeline/binding/save` — 保存绑定
- DELETE `/verto/appmanage/pipeline/binding/delete` — 删除绑定（`joinParamsToUrl: true`）
- GET `/verto/appmanage/pipeline/binding/detail` — 绑定详情
- GET `/verto/appmanage/pipeline/binding/validate` — 绑定校验（`jobName`）

### 4.4 Git 仓库相关
- POST `/verto/project/git/repo/create` — 新建仓库
- GET `/verto/project/git/permission/check` — 权限校验
- GET `/verto/project/git/repos` — 仓库列表
- GET `/verto/project/git/prefixes` — 仓库前缀（用于新建项目固定前缀）

- POST `/verto/appmanage/app/git/sync` — 同步应用的仓库信息（`joinParamsToUrl: true`）
- GET `/verto/appmanage/app/git/info` — 获取已持久化的仓库信息

### 4.5 Staff 复用
- GET `/verto/staff/active` — 在职人员列表（下拉选择）

---

## 5. Staff 模块接口（`src/views/staff/staff.api.ts`）

### 5.1 人员管理
- GET `/verto/staff/list` — 人员列表（列表页、积分管理筛选）
- GET `/verto/staff/queryById` — 人员详情（详情页/编辑页）
- POST `/verto/staff/add` — 新增人员
- PUT `/verto/staff/edit` — 编辑人员
- DELETE `/verto/staff/delete` — 删除人员（`joinParamsToUrl: true`）
- DELETE `/verto/staff/deleteBatch` — 批量删除（带确认对话框）
- GET `/verto/staff/importExcel` — 导入 URL（前端取地址使用）
- GET `/verto/staff/exportXls` — 导出 URL（前端取地址使用）

> 说明：`saveOrUpdateStaff` 根据 `isUpdate` 自动切换新增/编辑；`createStaff`、`updateStaff` 分别用于新增/编辑页面。

### 5.2 校验与字典/统计（部分待接入）
- GET `/sys/duplicate/check` — 重复性校验（`duplicateCheck`）
- 前端封装 `duplicateCheckDelay` — 300ms 延迟后请求上述接口，避免频繁校验
- GET `/verto/staff/checkEmployeeNo` — 工号重复校验
- GET `/verto/staff/checkEmail` — 邮箱重复校验
- GET `/sys/dict/getDictItems/staff_skills` — 技能字典
- GET `/verto/staff/skillsStats` — 技能统计
- GET `/verto/staff/departmentStats` — 部门统计

> 现状：`StaffCreate.vue`、`StaffEdit.vue` 未接入 `duplicateCheckDelay` / `checkEmployeeNo` / `checkEmail`；技能/部门统计暂未发现页面使用。

### 5.3 积分管理
- GET `/verto/staff/points/summary` — 人员总积分
  - 入参：`staffId`

- GET `/verto/staff/points/logs` — 人员积分流水（单人）
  - 入参：`staffId` + 可选筛选（时间区间、事件类型等）

- GET `/verto/staff/points/logs/all` — 全员积分流水（可选筛选）
  - 现状：未发现使用

- POST `/verto/staff/points/adjust` — 调整人员积分（正负均可）
  - 入参：`{ staffId, delta, remark?, source?: { sourceType?, sourceId?, eventType? } }`
  - 请求头：`X-PERMISSION: staff:points:adjust`
  - 用途：管理员手工调整或联调测试

---

## 6. 页面-接口使用映射（关键页面）
- `src/views/project/components/PipelineManager.vue`
  - 使用：`getPipelineHistory`、`triggerPipeline`、`retryBuild`、`getBuildLogs`、`getGitBranches`、`getGitCommits`、`getProjectDetail`

- `src/views/project/components/PipelineFlowChart.vue`
  - 仅 emit：`continueStage`、`retryStage`、`skipStage`、`cancelStage`、`viewStageLogs`（未发现父组件接入）

- `src/views/project/components/BuildDetailModal.vue`
  - 仅 emit：`continueStage`、`retryStage`、`viewStageLogs`（未发现父组件接入）

- `src/views/staff/StaffDetail.vue`
  - 使用：`getStaffById`、`getStaffPointsSummary`、`getStaffPointsLogs`

- `src/views/staff/StaffCreate.vue`
  - 使用：`createStaff`

- `src/views/staff/StaffEdit.vue`
  - 使用：`getStaffById`、`updateStaff`

- `src/views/staff/StaffDrawer.vue` / `src/views/staff/StaffModal.vue`
  - 使用：`saveOrUpdateStaff`

- `src/views/staff/StaffPointsManage.vue`
  - 使用：`getStaffList`、`getStaffPointsLogs`、`getStaffPointsSummary`、`adjustStaffPoints`

- `src/views/staff/index.vue`
  - 使用：`getStaffList`、`deleteStaff`、`batchDeleteStaff`、`getImportUrl`、`getExportUrl`

- `src/views/appmanage/*`（多个页面）
  - 使用：应用 CRUD、流水线配置与历史、绑定管理、Git 仓库相关、统计、包信息等接口（详见 4.2/4.3/4.4/4.5）。

---

## 7. 疑似未引用/待复核接口

- Project 模块：
  - 阶段操作：`continueStage`、`retryPipelineStage`、`skipPipelineStage`、`cancelStage`、`getStageLogs`（目前仅事件派发）
  - 部署相关：`deployPipeline`、`stopPipeline`

- Staff 模块：
  - 校验：`duplicateCheck`、`duplicateCheckDelay`、`checkEmployeeNo`、`checkEmail`
  - 字典/统计：`getSkillDict`、`getSkillsStats`、`getDepartmentStats`
  - 全员积分流水：`getAllStaffPointsLogs`

> 建议：在清理前进行全仓库检索与业务确认；如属预留功能，可在代码中添加注释说明其状态（Deprecated/预留/待接入）。

---

## 8. 维护建议

1) 一致性与显式约束
- 保持方法/路径与后端控制器注解一致（例如批量删除 `@DeleteMapping`）。
- 对 `joinParamsToUrl` 的使用保持统一，避免后端无法接收参数。

2) 阶段操作闭环
- 在接收 `PipelineFlowChart.vue` / `BuildDetailModal.vue` 事件的父组件中，接入并调用对应阶段接口，完善交互闭环。

3) 表单校验接入
- 在 `StaffCreate.vue` / `StaffEdit.vue` 的表单规则中接入 `duplicateCheckDelay` 或 `checkEmployeeNo`/`checkEmail`，实现即时校验并给出明确错误提示。

4) 文档化与示例
- 可在每个接口项下补充示例请求/响应结构（基于后端返回 JSON），便于联调与测试。

5) 版本标注
- 每次接口调整后，请更新本文件的“最后更新”时间与变更要点，保持团队认知一致。

---

## 9. 参考与来源

- 代码来源：
  - `verto-web/src/views/project/Project.api.ts`
  - `verto-web/src/views/appmanage/AppManage.api.ts`
  - `verto-web/src/views/staff/staff.api.ts`

- 后端参考：
  - `verto-server/jeecg-boot-module/jeecg-module-verto/.../AppManageController.java`（例如：`@DeleteMapping("/deleteBatch")`）