# 基于 verto_ 表的后端接口实现设计（与前端 mock 保持一致）

本文档描述如何在 verto-server 中实现真实后端接口，使其返回数据结构、字段命名和分页格式与前端 `verto-web/mock` 中的模拟接口保持完全一致。实现范围覆盖：应用管理、管理员关系、一对一模板绑定、流水线定义与运行历史、人员与积分系统。并给出 DTO 映射、Controller/Service/Mapper 的建议与示例代码片段，以及用于对齐 mock 的种子数据方案。

## 目标与约定

- 统一接口前缀：`/jeecgboot/verto`
- 统一返回结构：使用 Jeecg-Boot 的 `Result<T>`（等价于 mock 中的 `resultSuccess/resultPageSuccess/resultError`）
- 时间格式：统一返回字符串格式 `yyyy-MM-dd HH:mm:ss`
- 字段命名：后端数据库字段为 snake_case，接口返回统一为 camelCase
- 分页格式：`{ success: true, code: 0, message: 'ok', result: { total, pageNo, pageSize, items } }`
- 与 mock 完全一致的 ID 与枚举值：固定 ID（如 `app_1_1`、`pl_1`、`pl1_b15`）与枚举字符串（如 `PRODUCTION`、`DEVELOPMENT`）通过种子数据与 DTO 映射确保一致

## 涉及表与模块

- 人员：`verto_staff`
- 应用：`verto_application`
- 应用-管理员关系（多对多）：`verto_application_staff_manager`
- 物料模板：`verto_material_template`
- 应用-模板一对一关系：`verto_application_template_relation`
- 流水线定义：`verto_pipeline`
- 项目：`verto_project`
- 项目-流水线运行历史：`verto_project_pipeline`
- 积分流水：`verto_staff_points_log`（及扩展表，至少读取日志表即可满足 mock）

## 接口与前端 mock 对齐清单

### 应用管理（mock/appmanage-verto.ts）

- GET `/verto-backend/application/list`
  - 入参：`pageNo, pageSize, keyword`
  - 返回：分页 `ApplicationItem[]`
  - 字段映射：
    - `id` ← `verto_application.id`
    - `appName` ← `verto_application.app_name`
    - `appCode` ← 从 `verto_application.extra_info` JSON 中读取 `appCode`，若无则使用回退策略（如 `appName` 的大写下划线形式）
    - `appType` ← 从 `extra_info.appType` 读取（`WEB|API|MOBILE|OTHER`），若无则默认 `OTHER`
    - `status` ← 与 mock 枚举对齐：优先从 `extra_info.statusText` 读取；若仅有 `verto_application.status`（0/1），则映射：`1 -> PRODUCTION`，`0 -> DISABLED`
    - `description` ← `verto_application.app_description`
    - `gitUrl` ← `verto_application.git_url`
    - `createTime`/`updateTime` ← 格式化 `create_time`/`update_time`

- GET `/verto-backend/application/queryById`
  - 入参：`id`
  - 返回：`ApplicationItem & { managers: string[] }`
  - `managers` 来自 `verto_application_staff_manager` 关系表（按 `application_id` 查询 `staff_id` 列并返回数组）

- POST `/verto-backend/application/add`
  - 入参：`appName, appCode, appType, status, description, gitUrl`
  - 保存规则：核心字段写入 `verto_application`；`appCode/appType/statusText` 存入 `extra_info` JSON，确保后续查询返回与 mock 一致

- PUT `/verto-backend/application/edit`
  - 入参：包含 `id` 的编辑对象
  - 更新规则：同时维护 `extra_info` 中与 mock 字段相关的键值

- DELETE `/verto-backend/application/delete`
  - 删除应用并级联清理关系（管理员关系、一对一模板关系）

- GET `/verto-backend/application/managers/list`
  - 入参：`applicationId`
  - 返回：`{ applicationId, managers: string[] }`

- POST `/verto-backend/application/managers/bind`
  - 入参：`{ applicationId, staffIds: string[] }`
  - 逻辑：批量插入 `verto_application_staff_manager`（`uk_app_staff` 保证去重）

- POST `/verto-backend/application/managers/unbind`
  - 入参：`{ applicationId, staffIds: string[] }`
  - 逻辑：批量删除关系

- GET `/verto-backend/application/template/query`
  - 入参：`applicationId`
  - 返回：`{ applicationId, template: TemplateItem | null }`，其中 `TemplateItem` 由 `verto_material_template` 转为 camelCase

- POST `/verto-backend/application/template/bind`
  - 入参：`{ applicationId, templateId }`
  - 逻辑：`verto_application_template_relation` 一对一绑定（`uk_application_id`/`uk_template_id` 保证唯一），若已有绑定则覆盖

- POST `/verto-backend/application/template/unbind`
  - 入参：`{ applicationId }`
  - 逻辑：删除一对一关系，返回 `template: null`

### 流水线（mock/pipeline-verto.ts）

- GET `/verto-backend/pipeline/list`
  - 入参：`applicationId, pageNo, pageSize`
  - 返回：分页 `PipelineItem[]`
  - 字段映射：
    - `id` ← `verto_pipeline.id`
    - `applicationId` ← `verto_pipeline.application_id`
    - `name` ← `verto_pipeline.pipeline_name`
    - `enabled` ← `verto_pipeline.status == 'enabled'`
    - `autoTrigger`/`environments` ← 从 `verto_pipeline.config` JSON 读取并返回（示例：`{ autoTrigger: true, environments: [...] }`）
    - `description` 可放入 `config.description`

- GET `/verto-backend/pipeline/queryById`
  - 入参：`id`
  - 返回：`PipelineItem`

- POST `/verto-backend/pipeline/add`
  - 入参：`{ applicationId, name, description, enabled, autoTrigger, environments }`
  - 保存：`verto_pipeline` + 将 `autoTrigger/environments/description` 写入 `config` JSON；`enabled` 映射到 `status`（enabled/disabled）

- PUT `/verto-backend/pipeline/edit`
  - 入参：包含 `id` 的编辑对象
  - 更新：同上，更新 `config` 与 `status`

- DELETE `/verto-backend/pipeline/delete`
  - 逻辑：删除 `verto_pipeline` 并级联清理与其相关的运行历史（`verto_project_pipeline` 中引用该 `pipeline_id` 的记录）

- POST `/verto-backend/pipeline/run`
  - 入参：`{ pipelineId, branch='develop', author='system' }`
  - 返回：`{ buildId, pipelineId }`
  - 实现：在 `verto_project_pipeline` 新增一条运行记录，字段映射：
    - `pipeline_id` ← `pipelineId`
    - `project_id`：为了兼容 mock 未传 projectId 的情况，建议使用固定项目 ID（如 `PIPELINE_ONLY`），或新增配置项默认项目；并在种子数据中预置该项目
    - `build_number`：按该 `pipeline_id` 的历史最大值 + 1
    - `status`：初始 `running`
    - `start_time`：当前时间
    - `git_branch` ← 入参 `branch`
    - `git_commit`：生成随机短哈希
    - 其他字段（`commitMessage/currentStage/progress`）：可写入到 `config` JSON 或 `logs` 的结构化 JSON 中，返回时从 JSON 提取

- GET `/verto-backend/pipeline/status`
  - 入参：`pipelineId`
  - 返回：`{ pipelineId, isRunning, currentBuild }`
  - 实现：查询该 `pipelineId` 最新一条状态为 `running` 的记录并返回（`currentBuild` 字段需根据 `verto_project_pipeline` + JSON 字段拼装与 mock 一致）

- GET `/verto-backend/pipeline/history`
  - 入参：`pipelineId, pageNo, pageSize`
  - 返回：分页 `BuildRecord[]`
  - 字段映射：
    - `id` ← `verto_project_pipeline.id`
    - `pipelineId` ← `verto_project_pipeline.pipeline_id`
    - `number` ← `build_number`
    - `status` ← `status`
    - `startTime/endTime/duration/branch/commitId` ← 直接映射
    - `commitMessage/author/currentStage/progress` ← 来自 JSON（保存时写入）

- GET `/verto-backend/pipeline/build/detail`
  - 入参：`buildId`
  - 返回：`BuildRecord & { stages: { name, status, duration }[] }`
  - 实现：读取 `verto_project_pipeline`，同时从 JSON 字段（或 logs）中组装 `stages` 返回

- GET `/verto-backend/pipeline/build/logs/download`
  - 入参：`buildId`
  - 返回：`{ filename, content, size }`
  - 实现：从 `verto_project_pipeline.logs` 或外部日志存储中读取文本并返回

- POST `/verto-backend/pipeline/build/compare`
  - 入参：`{ pipelineId, buildIds: string[] }`
  - 返回：对比结果 JSON（结构与 mock 相同即可）

- DELETE `/verto-backend/pipeline/build/delete`
  - 入参：`buildId`
  - 实现：删除对应的历史记录；同时若是当前运行记录需置空运行状态

### 人员与积分（mock/staff_points.ts）

- GET `/verto-backend/staff/list`
  - 入参：`pageNo, pageSize, name, employeeNo, email, status`
  - 返回：分页人员，需附带 `points` 汇总字段（由积分日志求和）
  - 字段映射：
    - `id/name/employeeNo/email/phone/hireDate/workLocation/skills/status/remark` ← `verto_staff`
    - `points` ← 聚合 `verto_staff_points_log.delta` 之和

- GET `/verto-backend/staff/queryById`
  - 入参：`id`
  - 返回：`verto_staff` 对象（camelCase）

- GET `/verto-backend/staff/points/summary`
  - 入参：`staffId`
  - 返回：`{ totalPoints }`（`delta` 求和）

- GET `/verto-backend/staff/points/logs`
  - 入参：`staffId, pageNo, pageSize, eventType?, sourceType?`
  - 返回：分页积分流水，按时间倒序
  - 映射：`eventType/sourceType/sourceId/sourceName/delta/remark/createTime` ← `verto_staff_points_log`

- GET `/verto-backend/staff/points/logs/all`
  - 入参：`staffId?, pageNo, pageSize, eventType?, sourceType?, keyword?`
  - 返回：跨人员的分页积分流水（附带 `staffName`），并支持关键字筛选

- POST `/verto-backend/staff/points/adjust`
  - 入参：`{ staffId, delta, remark, sourceType, eventType, sourceId, sourceName }`
  - 逻辑：插入一条 `verto_staff_points_log`，并返回 `{ success: true }`

> 兼容别名：如需兼容 `/jeecgboot/staff/points/*`，可在 Controller 中复制一份路由或通过 Spring MVC 额外的 `@RequestMapping` 前缀实现。

## DTO 与字段映射策略

建议建立 DTO 层用于 camelCase 映射与 JSON 聚合，避免直接将 Entity 暴露给前端：

- ApplicationDTO：id, appName, appCode, appType, status, description, gitUrl, createTime, updateTime, managers?
- TemplateDTO：id, templateName, templateCode, version, authorStaffId, description, createTime
- PipelineDTO：id, applicationId, name, description, enabled, autoTrigger, environments, createTime, updateTime
- BuildRecordDTO：id, pipelineId, number, status, startTime, endTime, duration, branch, commitId, commitMessage, author, currentStage, progress
- StaffDTO：id, name, employeeNo, email, phone, hireDate, workLocation, skills, status, remark, points?
- PointsLogDTO：id, staffId, eventType, sourceType, sourceId, sourceName, delta, remark, createTime, staffName?

JSON 合并与回退策略：
- `verto_application.extra_info`：存储 `appCode`、`appType`、`statusText` 等前端所需但表结构未明确定义的字段
- `verto_pipeline.config`：存储 `autoTrigger`、`environments`、`description` 等结构化 JSON
- `verto_project_pipeline.config` 或 `logs`：存储 `commitMessage`、`author`、`currentStage`、`progress`、`stages` 等

## 代码结构建议（Jeecg-Boot / Spring Boot / MyBatis-Plus）

模块目录：`jeecg-boot-module/jeecg-module-verto`

- entity（使用 `@TableName`，字段 `@TableField` 标注 snake_case）：
  - `VertoApplication`（映射 `verto_application`）
  - `VertoApplicationStaffManager`（映射 `verto_application_staff_manager`）
  - `VertoMaterialTemplate`（映射 `verto_material_template`）
  - `VertoApplicationTemplateRelation`（映射 `verto_application_template_relation`）
  - `VertoPipeline`（映射 `verto_pipeline`）
  - `VertoProject`（映射 `verto_project`）
  - `VertoProjectPipeline`（映射 `verto_project_pipeline`）
  - `VertoStaff`（映射 `verto_staff`）
  - `VertoStaffPointsLog`（映射 `verto_staff_points_log`）

- mapper（`extends BaseMapper<Entity>`）：
  - 对应每个实体创建 Mapper 接口 + XML（如需复杂查询）

- service：
  - `ApplicationService`：列表/详情/新增/编辑/删除、管理员绑定/解绑、一对一模板绑定/解绑
  - `PipelineService`：列表/详情/新增/编辑/删除、运行、状态、历史、详情/日志、对比、删除构建
  - `StaffPointsService`：人员列表（带积分）、详情、积分汇总、日志分页、日志新增

- controller（路由严格对齐 mock）：
  - `ApplicationController`（`@RequestMapping("/verto-backend/application")`）
  - `PipelineController`（`@RequestMapping("/verto-backend/pipeline")`）
  - `StaffPointsController`（`@RequestMapping("/verto-backend/staff")`）
  - 如需兼容 `/jeecgboot`，可增设别名 Controller 或在类级别增加第二个 `@RequestMapping("/jeecgboot/...")`

### 示例代码片段（简化）

```java
// 返回封装
import org.jeecg.common.api.vo.Result;

@RestController
@RequestMapping("/verto-backend/application")
public class ApplicationController {
  @Autowired private ApplicationService applicationService;

  @GetMapping("/list")
  public Result<IPage<ApplicationDTO>> list(@RequestParam Integer pageNo,
                                            @RequestParam Integer pageSize,
                                            @RequestParam(required=false) String keyword) {
    IPage<ApplicationDTO> page = applicationService.pageList(pageNo, pageSize, keyword);
    return Result.OK(page);
  }

  @GetMapping("/queryById")
  public Result<ApplicationDTO> queryById(@RequestParam String id) {
    return Result.OK(applicationService.detail(id));
  }
}
```

```java
// Service 片段：extra_info JSON 映射
public class ApplicationService {
  public ApplicationDTO toDTO(VertoApplication app) {
    ApplicationDTO dto = new ApplicationDTO();
    dto.setId(app.getId());
    dto.setAppName(app.getAppName());
    JsonNode extra = Jsons.parseOrNull(app.getExtraInfo());
    dto.setAppCode(Jsons.getString(extra, "appCode", deriveCode(app.getAppName())));
    dto.setAppType(Jsons.getString(extra, "appType", "OTHER"));
    dto.setStatus(Jsons.getString(extra, "statusText", app.getStatus() != null && app.getStatus() == 1 ? "PRODUCTION" : "DISABLED"));
    dto.setDescription(app.getAppDescription());
    dto.setGitUrl(app.getGitUrl());
    dto.setCreateTime(DateFmt.format(app.getCreateTime()));
    dto.setUpdateTime(DateFmt.format(app.getUpdateTime()));
    // managers
    dto.setManagers(applicationStaffManagerMapper.selectStaffIdsByAppId(app.getId()));
    return dto;
  }
}
```

## 与 mock 一致性的关键点

- 固定 ID 与枚举：通过种子数据提前插入（见下文），接口按真实查询返回即可与 mock 对齐
- 字段缺失的回退与 JSON 扩展：充分利用 `extra_info/config/logs` JSON 存储前端所需但表结构没有的字段
- 分页与排序：列表默认按创建时间或更新时间倒序，与 mock 行为一致
- 错误提示：使用 `Result.error(msg)`，文案与 mock 相同（如“应用不存在”“流水线不存在”）

## 种子数据（建议）

为保证真实接口与 mock 一致，建议新增一个种子脚本：`db/seeds/2025-11-04-mock-aligned-seed.sql`，插入以下数据：

- 应用（`verto_application`）：`app_1_1`、`app_1_2`、`app_2_1`、`app_3_1`
  - `extra_info` JSON 包含 `appCode/appType/statusText`
- 应用管理员关系（`verto_application_staff_manager`）：见 mock 中的 `appManagers`
- 模板（`verto_material_template`）：`tpl_001`、`tpl_002`
- 应用-模板关系（`verto_application_template_relation`）：绑定 `app_1_1->tpl_001`, `app_1_2->tpl_002`
- 流水线（`verto_pipeline`）：`pl_1`（`application_id=app_1_1`）、`pl_2`（`app_1_2`）、`pl_3`（`app_2_1`）
  - `config` JSON 含 `autoTrigger/environments/description`
- 运行历史（`verto_project_pipeline`）：`pl1_b15`、`pl2_b7` 等，`project_id` 使用固定占位项目 `PIPELINE_ONLY`
  - 相关 JSON 放入 `config` 或 `logs` 以便返回 `commitMessage/author/currentStage/progress/stages`
- 人员（`verto_staff`）与积分流水（`verto_staff_points_log`）：按 mock 中示例预置若干人及对应日志，保证列表与汇总一致

> 注意：如果现有环境不希望引入固定 ID，可在 Controller 层增加“模拟数据模式”开关，仅在开发环境返回与 mock 一致的数据；生产环境关闭开关，返回真实数据。

## 开发步骤清单

1) 创建实体与 Mapper（MyBatis-Plus），校验表名/字段/主键/索引与 `2025-11-04-complete-verto-schema.sql` 一致。
2) 编写 DTO 与 MapStruct（或手写转换），统一 camelCase 返回；补充 JSON 字段的合并逻辑。
3) 编写 Service 实现业务逻辑（含事务）：绑定/解绑、模板一对一维护、流水线运行/状态、积分汇总/分页等。
4) 编写 Controller，路由严格对齐 mock；分页与错误提示保持一致；必要时增加 `/jeecgboot` 前缀兼容路由。
5) 增加种子数据脚本并在本地开发环境初始化；联调确认返回内容与 mock 完全一致。
6) 编写自测说明/接口清单（Postman/Apifox），并在 `verto-server/docs` 中维护。

## 风险与回避

- 表结构与前端字段不完全对应：通过 `extra_info/config` JSON 扩展与 DTO 回退策略解决。
- `verto_project_pipeline` 的 `project_id` 非空约束与 mock 的“仅 pipeline 运行”不一致：使用固定占位项目或增加默认项目配置解决。
- 时间格式与时区差异：统一 `SimpleDateFormat("yyyy-MM-dd HH:mm:ss")` 并设置时区。

## 附：建议的包结构

```
jeecg-boot-module/jeecg-module-verto
  ├─ controller
  │   ├─ ApplicationController.java
  │   ├─ PipelineController.java
  │   └─ StaffPointsController.java
  ├─ service
  │   ├─ ApplicationService.java
  │   ├─ PipelineService.java
  │   └─ StaffPointsService.java
  ├─ service/impl
  ├─ entity
  ├─ mapper
  │   ├─ xml（如需）
  ├─ dto
  ├─ util（Jsons/DateFmt 等）
  └─ config（可选：路由前缀与别名、开发模式开关）
```

---

如需我继续：
- 生成上述 Controller/Service/Entity 的代码骨架与 DTO；
- 编写 `db/seeds/2025-11-04-mock-aligned-seed.sql`；
- 在 docs 中补充接口示例与 Postman 集合。