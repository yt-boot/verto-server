# 基于 verto_ 表结构的前端 Mock 接口生成指南

本文档说明如何根据 `db/2025-11-04-complete-verto-schema.sql` 的最新数据库表结构，在 `verto-web/mock` 目录下生成/调整前端 Mock 接口，供前端联调与演示使用。

## 目标与约定

- 前端 Mock 接口统一使用前缀：`/verto-backend`，与现有部分 `jeecgboot` 接口并存，逐步完成前端的接口切换。
- 表命名均为 `verto_` 前缀，与数据库保持一致。
- 返回结构统一使用已有工具：`resultSuccess`、`resultPageSuccess`、`resultError`（见 `verto-web/mock/_util.ts`）。

## 涉及的核心表与接口映射

1) 人员管理（`verto_staff`）与积分系统（`verto_staff_points_*`）
- 已有 Mock 文件：`verto-web/mock/staff_points.ts`
- 已提供接口（部分示例）：
  - GET `/verto-backend/staff/list` 人员分页
  - GET `/verto-backend/staff/queryById` 人员详情
  - GET `/verto-backend/staff/points/summary` 人员积分汇总（来源：`verto_staff_points_balance` + 计算日志）
  - GET `/verto-backend/staff/points/logs` 人员积分流水（对应 `verto_staff_points_log`）
  - POST `/verto-backend/staff/points/adjust` 人工调整积分（写入 `verto_staff_points_log`，并触发去重/规则校验的模拟）

2) 应用管理与管理员关系（`verto_application`、`verto_application_staff_manager`）
- 新增 Mock 文件：`verto-web/mock/appmanage-verto.ts`
- 接口：
  - GET `/verto-backend/application/list` 应用分页列表（`verto_application`）
  - GET `/verto-backend/application/queryById` 应用详情（含管理员聚合）
  - POST `/verto-backend/application/add` 新增应用
  - PUT `/verto-backend/application/edit` 编辑应用
  - DELETE `/verto-backend/application/delete` 删除应用（清理管理员与模板关系）
  - GET `/verto-backend/application/managers/list?applicationId=...` 查询应用管理员（`verto_application_staff_manager`）
  - POST `/verto-backend/application/managers/bind` 绑定管理员（批量）
  - POST `/verto-backend/application/managers/unbind` 解绑管理员（批量）

3) 模板与应用一对一关系（`verto_material_template`、`verto_application_template_relation`）
- 集成于 `appmanage-verto.ts`
- 接口：
  - GET `/verto-backend/application/template/query?applicationId=...` 查询应用绑定模板
  - POST `/verto-backend/application/template/bind` 绑定模板（覆盖式一对一）
  - POST `/verto-backend/application/template/unbind` 解绑模板

4) 流水线定义与运行（`verto_pipeline`、`verto_project_pipeline`）
- 新增 Mock 文件：`verto-web/mock/pipeline-verto.ts`
- 接口：
  - GET `/verto-backend/pipeline/list?applicationId=...` 列出应用下的流水线定义
  - GET `/verto-backend/pipeline/queryById?id=...` 流水线详情
  - POST `/verto-backend/pipeline/add` 新增流水线（绑定到应用）
  - PUT `/verto-backend/pipeline/edit` 编辑流水线
  - DELETE `/verto-backend/pipeline/delete?id=...` 删除流水线（同时清理运行历史）
  - POST `/verto-backend/pipeline/run` 触发运行（生成构建记录，模拟 `verto_project_pipeline` 的运行历史）
  - GET `/verto-backend/pipeline/status?pipelineId=...` 当前运行状态
  - GET `/verto-backend/pipeline/history?pipelineId=...` 构建历史分页
  - GET `/verto-backend/pipeline/build/detail?buildId=...` 构建详情（含阶段信息）
  - GET `/verto-backend/pipeline/build/logs/download?buildId=...` 下载构建日志（模拟文本）
  - POST `/verto-backend/pipeline/build/compare` 对比构建（两次构建差异）
  - DELETE `/verto-backend/pipeline/build/delete?buildId=...` 删除某次构建记录

5) 项目与人员多对多（`verto_project`、`verto_project_staff_relation`）
- 当前 UI 使用 `verto-web/mock/project.ts` 中的 `/jeecgboot/project/...` 路径。
- 后续建议新增 `project-verto.ts`（同风格），接口示例：
  - GET `/verto-backend/project/list`、`queryById`、`add`、`edit`、`delete`
  - GET `/verto-backend/project/staff/list?projectId=...`
  - POST `/verto-backend/project/staff/bind`、`/unbind`

6) OAuth（`verto_oauth_user`、`verto_oauth_token`、`verto_oauth_binding`）
- 如需前端登录态与第三方绑定演示，可另建 `oauth-verto.ts`：
  - GET `/verto-backend/oauth/user/queryById`、`/binding/list`
  - POST `/verto-backend/oauth/token/issue`、`/binding/add`、`/binding/remove`

## Mock 代码生成与组织

目录：`verto-web/mock`

- 已新增/更新文件：
  - `appmanage-verto.ts`：应用、管理员、多对多关系、模板一对一绑定
  - `pipeline-verto.ts`：流水线定义与运行、历史记录与日志
  - 复用：`staff_points.ts` 中的人员与积分接口（含 `/verto-backend` 前缀）

编码规范：

- 每个接口节点导出为 `MockMethod[]`，示例：
```ts
export default [
  {
    url: '/verto-backend/application/list',
    method: 'get',
    response: ({ query }) => { /* 返回 resultPageSuccess(...) */ },
  },
];
```
- 统一返回：
  - 成功：`resultSuccess(data)`/`resultPageSuccess(pageNo, pageSize, list)`
  - 失败：`resultError('错误信息')`
- 字段命名与 SQL 表结构保持一致或提供显式映射（如 `appName` 对应 `verto_application.name`）。

## 与现有 `/jeecgboot` 接口的兼容策略

- 保留现有 `project.ts`、`pipeline.ts`、`project-pipeline.ts` 的 `/jeecgboot/...` 路径以兼容当前 UI。
- 新增 `/verto-backend/...` 路径后，前端可逐步切换，或在 Axios 层通过配置统一前缀。
- 示例：`staff_points.ts` 已同时提供 `/verto-backend` 与无前缀别名（兼容模式）。

## 示例请求/响应（节选）

1) 获取应用列表
```
GET /verto-backend/application/list?pageNo=1&pageSize=10&keyword=ecom
=> { code:0, success:true, result:{ records:[...], total: N }, message:'ok' }
```

2) 绑定应用管理员
```
POST /verto-backend/application/managers/bind
Body: { applicationId:'app_2_1', staffIds:['2','4'] }
=> { code:0, success:true, result:{ applicationId:'app_2_1', managers:['2','4'] } }
```

3) 触发流水线运行
```
POST /verto-backend/pipeline/run
Body: { pipelineId:'pl_3', branch:'develop' }
=> { code:0, success:true, result:{ buildId:'build_pl_3_...', pipelineId:'pl_3' } }
```

## 后续扩展建议

- 新增 `project-verto.ts`，对接 `verto_project` 与 `verto_project_staff_relation`；
- 新增 `oauth-verto.ts`，演示 OAuth 三表的基本交互；
- 在 `verto-web/src/api` 中增加类型定义（TypeScript interfaces），直接映射至 SQL 字段；
- 根据 `verto_project_pipeline` 的具体业务需要，细化构建阶段、环境变量、通知策略的字段。

## 变更记录

- 2025-11-04：首次创建，基于 `2025-11-04-complete-verto-schema.sql` 生成 Mock 接口与指南。