package com.verto.common.api;

/**
 * 通用常量类
 * 
 * @author Verto Team
 * @since 2024-01-01
 */
public interface CommonConstant {

    /**
     * 正常状态
     */
    Integer STATUS_1 = 1;

    /**
     * 禁用状态
     */
    Integer STATUS_0 = 0;

    /**
     * 删除标志
     */
    Integer DEL_FLAG_1 = 1;

    /**
     * 未删除
     */
    Integer DEL_FLAG_0 = 0;

    /**
     * 系统日志类型： 登录
     */
    int LOG_TYPE_1 = 1;

    /**
     * 系统日志类型： 操作
     */
    int LOG_TYPE_2 = 2;

    /**
     * 操作日志类型： 查询
     */
    int OPERATE_TYPE_1 = 1;

    /**
     * 操作日志类型： 添加
     */
    int OPERATE_TYPE_2 = 2;

    /**
     * 操作日志类型： 更新
     */
    int OPERATE_TYPE_3 = 3;

    /**
     * 操作日志类型： 删除
     */
    int OPERATE_TYPE_4 = 4;

    /**
     * 操作日志类型： 倒入
     */
    int OPERATE_TYPE_5 = 5;

    /**
     * 操作日志类型： 导出
     */
    int OPERATE_TYPE_6 = 6;

    /**
     * {@code 500 Server Error} (HTTP/1.0 - RFC 1945)
     */
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /**
     * {@code 200 OK} (HTTP/1.0 - RFC 1945)
     */
    public static final Integer SC_OK_200 = 200;

    /**
     * 访问权限认证未通过 510
     */
    public static final Integer SC_JEECG_NO_AUTHZ = 510;

    /**
     * 登录用户Shiro权限缓存KEY前缀
     */
    public static final String PREFIX_USER_SHIRO_CACHE = "shiro:cache:org.jeecg.config.shiro.ShiroRealm.authorizationCache:";
    /**
     * 登录用户Token令牌缓存KEY前缀
     */
    public static final String PREFIX_USER_TOKEN = "prefix_user_token_";
    /**
     * Token缓存时间：3600秒即一小时
     */
    public static final int TOKEN_EXPIRE_TIME = 3600;

    /**
     * 0：一级菜单
     */
    public static final Integer MENU_TYPE_0 = 0;
    /**
     * 1：子菜单
     */
    public static final Integer MENU_TYPE_1 = 1;
    /**
     * 2：按钮权限
     */
    public static final Integer MENU_TYPE_2 = 2;

    /**
     * 通告对象类型（USER:指定用户，ALL:全体用户）
     */
    public static final String MSG_TYPE_UESR = "USER";
    public static final String MSG_TYPE_ALL = "ALL";

    /**
     * 发布状态（0未发布，1已发布，2已撤销）
     */
    public static final String NO_SEND = "0";
    public static final String HAS_SEND = "1";
    public static final String HAS_CANCLE = "2";

    /**
     * 阅读状态（0未读，1已读）
     */
    public static final String HAS_READ_FLAG = "1";
    public static final String NO_READ_FLAG = "0";

    /**
     * 优先级（L低，M中，H高）
     */
    public static final String PRIORITY_L = "L";
    public static final String PRIORITY_M = "M";
    public static final String PRIORITY_H = "H";

    /**
     * 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
     */
    public static final String SMS_TPL_TYPE_0 = "0";
    public static final String SMS_TPL_TYPE_1 = "1";
    public static final String SMS_TPL_TYPE_2 = "2";

    /**
     * 状态(0无效1有效)
     */
    // public static final String STATUS_0 = "0";  // 已在接口顶部定义为 Integer 类型
    // public static final String STATUS_1 = "1";  // 已在接口顶部定义为 Integer 类型

    /**
     * 同步工作流引擎1同步0不同步
     */
    public static final Integer ACT_SYNC_1 = 1;
    public static final Integer ACT_SYNC_0 = 0;

    /**
     * 消息类型1:通知公告2:系统消息
     */
    public static final String MSG_CATEGORY_1 = "1";
    public static final String MSG_CATEGORY_2 = "2";

    /**
     * 是否配置菜单的数据权限 1是0否
     */
    public static final Integer RULE_FLAG_0 = 0;
    public static final Integer RULE_FLAG_1 = 1;

    /**
     * 是否用户已被冻结 1正常(解冻) 2冻结
     */
    public static final Integer USER_UNFREEZE = 1;
    public static final Integer USER_FREEZE = 2;

    /**
     * 字典翻译文本后缀
     */
    public static final String DICT_TEXT_SUFFIX = "_dictText";

    /**
     * 表单设计器主表类型
     */
    public static final Integer DESIGN_FORM_TYPE_MAIN = 1;

    /**
     * 表单设计器子表类型
     */
    public static final Integer DESIGN_FORM_TYPE_SUB = 2;

    /**
     * 表单设计器URL类型
     */
    public static final Integer DESIGN_FORM_TYPE_URL = 3;

    /**
     * 表单设计器查询模式
     */
    public static final String DESIGN_FORM_QUERY_MODE = "single";

    /**
     * 高级查询
     */
    public static final String SUPER_QUERY_PARAMS = "superQueryParams";

    /**
     * 高级查询匹配规则
     */
    public static final String SUPER_QUERY_MATCH_TYPE = "superQueryMatchType";

    /**
     * Excel导出全量数据
     */
    public static final String EXPORT_ALL_DATA = "all";

    /**
     * Excel导出选中数据
     */
    public static final String EXPORT_CHECKED_DATA = "selected";

    /**
     * 系统通告消息状态：0=未发布
     */
    public static final String ANNOUNCEMENT_SEND_STATUS_0 = "0";

    /**
     * 系统通告消息状态：1=已发布
     */
    public static final String ANNOUNCEMENT_SEND_STATUS_1 = "1";

    /**
     * 系统通告消息状态：2=已撤销
     */
    public static final String ANNOUNCEMENT_SEND_STATUS_2 = "2";

    /**
     * 文件上传类型（本地：local，Minio：minio，阿里云：alioss）
     */
    public static final String UPLOAD_TYPE_LOCAL = "local";
    public static final String UPLOAD_TYPE_MINIO = "minio";
    public static final String UPLOAD_TYPE_OSS = "alioss";

    /**
     * 部门状态标识
     */
    public static final String DEPART_STATUS_1 = "1";

    /**
     * 同步钉钉审批人1同步0不同步
     */
    public static final String DINGTALK_PROCESS_SYNC_1 = "1";
    public static final String DINGTALK_PROCESS_SYNC_0 = "0";

    /**
     * 第三方登录身份标识
     */
    public static final String THIRD_LOGIN_CODE = "third_login_code";

    /**
     * 系统生成的
     */
    public static final String RULE_FLAG_SYSTEM = "1";

    /**
     * 用户生成的
     */
    public static final String RULE_FLAG_USER = "0";

    /**
     * 组件规则：可见
     */
    public static final String COMPONENT_RULE_VISIBLE = "visible";

    /**
     * 组件规则：禁用
     */
    public static final String COMPONENT_RULE_DISABLE = "disable";

    /**
     * 组件规则：必填
     */
    public static final String COMPONENT_RULE_REQUIRED = "required";

    /**
     * 组件规则：只读
     */
    public static final String COMPONENT_RULE_READONLY = "readonly";

    /**
     * 组件规则：隐藏
     */
    public static final String COMPONENT_RULE_HIDDEN = "hidden";

    /**
     * 组件规则：显示
     */
    public static final String COMPONENT_RULE_DISPLAY = "display";

    /**
     * 组件规则：可编辑
     */
    public static final String COMPONENT_RULE_EDITABLE = "editable";

    /**
     * 组件规则：可选择
     */
    public static final String COMPONENT_RULE_OPTIONAL = "optional";

    /**
     * 组件规则：可点击
     */
    public static final String COMPONENT_RULE_CLICKABLE = "clickable";

    /**
     * 组件规则：可拖拽
     */
    public static final String COMPONENT_RULE_DRAGGABLE = "draggable";

    /**
     * 组件规则：可排序
     */
    public static final String COMPONENT_RULE_SORTABLE = "sortable";

    /**
     * 组件规则：可搜索
     */
    public static final String COMPONENT_RULE_SEARCHABLE = "searchable";

    /**
     * 组件规则：可过滤
     */
    public static final String COMPONENT_RULE_FILTERABLE = "filterable";

    /**
     * 组件规则：可分页
     */
    public static final String COMPONENT_RULE_PAGEABLE = "pageable";

    /**
     * 组件规则：可导出
     */
    public static final String COMPONENT_RULE_EXPORTABLE = "exportable";

    /**
     * 组件规则：可导入
     */
    public static final String COMPONENT_RULE_IMPORTABLE = "importable";

    /**
     * 组件规则：可打印
     */
    public static final String COMPONENT_RULE_PRINTABLE = "printable";

    /**
     * 组件规则：可复制
     */
    public static final String COMPONENT_RULE_COPYABLE = "copyable";

    /**
     * 组件规则：可删除
     */
    public static final String COMPONENT_RULE_DELETABLE = "deletable";

    /**
     * 组件规则：可编辑
     */
    public static final String COMPONENT_RULE_UPDATABLE = "updatable";

    /**
     * 组件规则：可新增
     */
    public static final String COMPONENT_RULE_ADDABLE = "addable";

    /**
     * 组件规则：可查看
     */
    public static final String COMPONENT_RULE_VIEWABLE = "viewable";

    /**
     * 组件规则：可下载
     */
    public static final String COMPONENT_RULE_DOWNLOADABLE = "downloadable";

    /**
     * 组件规则：可上传
     */
    public static final String COMPONENT_RULE_UPLOADABLE = "uploadable";

    /**
     * 组件规则：可刷新
     */
    public static final String COMPONENT_RULE_REFRESHABLE = "refreshable";

    /**
     * 组件规则：可重置
     */
    public static final String COMPONENT_RULE_RESETTABLE = "resettable";

    /**
     * 组件规则：可提交
     */
    public static final String COMPONENT_RULE_SUBMITTABLE = "submittable";

    /**
     * 组件规则：可取消
     */
    public static final String COMPONENT_RULE_CANCELABLE = "cancelable";

    /**
     * 组件规则：可保存
     */
    public static final String COMPONENT_RULE_SAVEABLE = "saveable";

    /**
     * 组件规则：可关闭
     */
    public static final String COMPONENT_RULE_CLOSEABLE = "closeable";

    /**
     * 组件规则：可最小化
     */
    public static final String COMPONENT_RULE_MINIMIZABLE = "minimizable";

    /**
     * 组件规则：可最大化
     */
    public static final String COMPONENT_RULE_MAXIMIZABLE = "maximizable";

    /**
     * 组件规则：可还原
     */
    public static final String COMPONENT_RULE_RESTORABLE = "restorable";

    /**
     * 组件规则：可移动
     */
    public static final String COMPONENT_RULE_MOVABLE = "movable";

    /**
     * 组件规则：可调整大小
     */
    public static final String COMPONENT_RULE_RESIZABLE = "resizable";

    /**
     * 组件规则：可折叠
     */
    public static final String COMPONENT_RULE_COLLAPSIBLE = "collapsible";

    /**
     * 组件规则：可展开
     */
    public static final String COMPONENT_RULE_EXPANDABLE = "expandable";

    /**
     * 组件规则：可选中
     */
    public static final String COMPONENT_RULE_SELECTABLE = "selectable";

    /**
     * 组件规则：可多选
     */
    public static final String COMPONENT_RULE_MULTI_SELECTABLE = "multi_selectable";

    /**
     * 组件规则：可单选
     */
    public static final String COMPONENT_RULE_SINGLE_SELECTABLE = "single_selectable";

    /**
     * 组件规则：可全选
     */
    public static final String COMPONENT_RULE_SELECT_ALL = "select_all";

    /**
     * 组件规则：可反选
     */
    public static final String COMPONENT_RULE_INVERT_SELECTION = "invert_selection";

    /**
     * 组件规则：可清空选择
     */
    public static final String COMPONENT_RULE_CLEAR_SELECTION = "clear_selection";

    /**
     * 组件规则：可验证
     */
    public static final String COMPONENT_RULE_VALIDATABLE = "validatable";

    /**
     * 组件规则：可格式化
     */
    public static final String COMPONENT_RULE_FORMATTABLE = "formattable";

    /**
     * 组件规则：可转换
     */
    public static final String COMPONENT_RULE_CONVERTIBLE = "convertible";

    /**
     * 组件规则：可计算
     */
    public static final String COMPONENT_RULE_CALCULABLE = "calculable";

    /**
     * 组件规则：可统计
     */
    public static final String COMPONENT_RULE_STATISTICAL = "statistical";

    /**
     * 组件规则：可分组
     */
    public static final String COMPONENT_RULE_GROUPABLE = "groupable";

    /**
     * 组件规则：可聚合
     */
    public static final String COMPONENT_RULE_AGGREGATABLE = "aggregatable";

    /**
     * 组件规则：可关联
     */
    public static final String COMPONENT_RULE_RELATABLE = "relatable";

    /**
     * 组件规则：可链接
     */
    public static final String COMPONENT_RULE_LINKABLE = "linkable";

    /**
     * 组件规则：可嵌入
     */
    public static final String COMPONENT_RULE_EMBEDDABLE = "embeddable";

    /**
     * 组件规则：可扩展
     */
    public static final String COMPONENT_RULE_EXTENSIBLE = "extensible";

    /**
     * 组件规则：可配置
     */
    public static final String COMPONENT_RULE_CONFIGURABLE = "configurable";

    /**
     * 组件规则：可定制
     */
    public static final String COMPONENT_RULE_CUSTOMIZABLE = "customizable";

    /**
     * 组件规则：可个性化
     */
    public static final String COMPONENT_RULE_PERSONALIZABLE = "personalizable";

    /**
     * 组件规则：可主题化
     */
    public static final String COMPONENT_RULE_THEMEABLE = "themeable";

    /**
     * 组件规则：可国际化
     */
    public static final String COMPONENT_RULE_INTERNATIONALIZABLE = "internationalizable";

    /**
     * 组件规则：可本地化
     */
    public static final String COMPONENT_RULE_LOCALIZABLE = "localizable";

    /**
     * 组件规则：可缓存
     */
    public static final String COMPONENT_RULE_CACHEABLE = "cacheable";

    /**
     * 组件规则：可持久化
     */
    public static final String COMPONENT_RULE_PERSISTABLE = "persistable";

    /**
     * 组件规则：可序列化
     */
    public static final String COMPONENT_RULE_SERIALIZABLE = "serializable";

    /**
     * 组件规则：可反序列化
     */
    public static final String COMPONENT_RULE_DESERIALIZABLE = "deserializable";

    /**
     * 组件规则：可压缩
     */
    public static final String COMPONENT_RULE_COMPRESSIBLE = "compressible";

    /**
     * 组件规则：可解压
     */
    public static final String COMPONENT_RULE_DECOMPRESSIBLE = "decompressible";

    /**
     * 组件规则：可加密
     */
    public static final String COMPONENT_RULE_ENCRYPTABLE = "encryptable";

    /**
     * 组件规则：可解密
     */
    public static final String COMPONENT_RULE_DECRYPTABLE = "decryptable";

    /**
     * 组件规则：可签名
     */
    public static final String COMPONENT_RULE_SIGNABLE = "signable";

    /**
     * 组件规则：可验签
     */
    public static final String COMPONENT_RULE_VERIFIABLE = "verifiable";

    /**
     * 组件规则：可审计
     */
    public static final String COMPONENT_RULE_AUDITABLE = "auditable";

    /**
     * 组件规则：可监控
     */
    public static final String COMPONENT_RULE_MONITORABLE = "monitorable";

    /**
     * 组件规则：可告警
     */
    public static final String COMPONENT_RULE_ALERTABLE = "alertable";

    /**
     * 组件规则：可通知
     */
    public static final String COMPONENT_RULE_NOTIFIABLE = "notifiable";

    /**
     * 组件规则：可订阅
     */
    public static final String COMPONENT_RULE_SUBSCRIBABLE = "subscribable";

    /**
     * 组件规则：可发布
     */
    public static final String COMPONENT_RULE_PUBLISHABLE = "publishable";

    /**
     * 组件规则：可广播
     */
    public static final String COMPONENT_RULE_BROADCASTABLE = "broadcastable";

    /**
     * 组件规则：可多播
     */
    public static final String COMPONENT_RULE_MULTICASTABLE = "multicastable";

    /**
     * 组件规则：可单播
     */
    public static final String COMPONENT_RULE_UNICASTABLE = "unicastable";

    /**
     * 组件规则：可路由
     */
    public static final String COMPONENT_RULE_ROUTABLE = "routable";

    /**
     * 组件规则：可负载均衡
     */
    public static final String COMPONENT_RULE_LOAD_BALANCEABLE = "load_balanceable";

    /**
     * 组件规则：可容错
     */
    public static final String COMPONENT_RULE_FAULT_TOLERANT = "fault_tolerant";

    /**
     * 组件规则：可恢复
     */
    public static final String COMPONENT_RULE_RECOVERABLE = "recoverable";

    /**
     * 组件规则：可回滚
     */
    public static final String COMPONENT_RULE_ROLLBACKABLE = "rollbackable";

    /**
     * 组件规则：可重试
     */
    public static final String COMPONENT_RULE_RETRYABLE = "retryable";

    /**
     * 组件规则：可超时
     */
    public static final String COMPONENT_RULE_TIMEOUTABLE = "timeoutable";

    /**
     * 组件规则：可限流
     */
    public static final String COMPONENT_RULE_RATE_LIMITABLE = "rate_limitable";

    /**
     * 组件规则：可熔断
     */
    public static final String COMPONENT_RULE_CIRCUIT_BREAKABLE = "circuit_breakable";

    /**
     * 组件规则：可降级
     */
    public static final String COMPONENT_RULE_DEGRADABLE = "degradable";

    /**
     * 组件规则：可隔离
     */
    public static final String COMPONENT_RULE_ISOLATABLE = "isolatable";

    /**
     * 组件规则：可池化
     */
    public static final String COMPONENT_RULE_POOLABLE = "poolable";

    /**
     * 组件规则：可连接
     */
    public static final String COMPONENT_RULE_CONNECTABLE = "connectable";

    /**
     * 组件规则：可断开
     */
    public static final String COMPONENT_RULE_DISCONNECTABLE = "disconnectable";

    /**
     * 组件规则：可重连
     */
    public static final String COMPONENT_RULE_RECONNECTABLE = "reconnectable";

    /**
     * 组件规则：可心跳
     */
    public static final String COMPONENT_RULE_HEARTBEATABLE = "heartbeatable";

    /**
     * 组件规则：可健康检查
     */
    public static final String COMPONENT_RULE_HEALTH_CHECKABLE = "health_checkable";

    /**
     * 组件规则：可就绪检查
     */
    public static final String COMPONENT_RULE_READINESS_CHECKABLE = "readiness_checkable";

    /**
     * 组件规则：可存活检查
     */
    public static final String COMPONENT_RULE_LIVENESS_CHECKABLE = "liveness_checkable";

    /**
     * 组件规则：可启动检查
     */
    public static final String COMPONENT_RULE_STARTUP_CHECKABLE = "startup_checkable";

    /**
     * 组件规则：可关闭检查
     */
    public static final String COMPONENT_RULE_SHUTDOWN_CHECKABLE = "shutdown_checkable";

    /**
     * 组件规则：可预热
     */
    public static final String COMPONENT_RULE_WARMUPABLE = "warmupable";

    /**
     * 组件规则：可冷却
     */
    public static final String COMPONENT_RULE_COOLDOWNABLE = "cooldownable";

    /**
     * 组件规则：可暂停
     */
    public static final String COMPONENT_RULE_PAUSABLE = "pausable";

    /**
     * 组件规则：可恢复
     */
    public static final String COMPONENT_RULE_RESUMABLE = "resumable";

    /**
     * 组件规则：可停止
     */
    public static final String COMPONENT_RULE_STOPPABLE = "stoppable";

    /**
     * 组件规则：可重启
     */
    public static final String COMPONENT_RULE_RESTARTABLE = "restartable";

    /**
     * 组件规则：可销毁
     */
    public static final String COMPONENT_RULE_DESTROYABLE = "destroyable";

    /**
     * 组件规则：可释放
     */
    public static final String COMPONENT_RULE_RELEASABLE = "releasable";

    /**
     * 组件规则：可清理
     */
    public static final String COMPONENT_RULE_CLEANABLE = "cleanable";

    /**
     * 组件规则：可垃圾回收
     */
    public static final String COMPONENT_RULE_GARBAGE_COLLECTABLE = "garbage_collectable";

    /**
     * 组件规则：可内存管理
     */
    public static final String COMPONENT_RULE_MEMORY_MANAGEABLE = "memory_manageable";

    /**
     * 组件规则：可资源管理
     */
    public static final String COMPONENT_RULE_RESOURCE_MANAGEABLE = "resource_manageable";

    /**
     * 组件规则：可生命周期管理
     */
    public static final String COMPONENT_RULE_LIFECYCLE_MANAGEABLE = "lifecycle_manageable";

    /**
     * 组件规则：可状态管理
     */
    public static final String COMPONENT_RULE_STATE_MANAGEABLE = "state_manageable";

    /**
     * 组件规则：可事件管理
     */
    public static final String COMPONENT_RULE_EVENT_MANAGEABLE = "event_manageable";

    /**
     * 组件规则：可消息管理
     */
    public static final String COMPONENT_RULE_MESSAGE_MANAGEABLE = "message_manageable";

    /**
     * 组件规则：可任务管理
     */
    public static final String COMPONENT_RULE_TASK_MANAGEABLE = "task_manageable";

    /**
     * 组件规则：可作业管理
     */
    public static final String COMPONENT_RULE_JOB_MANAGEABLE = "job_manageable";

    /**
     * 组件规则：可调度管理
     */
    public static final String COMPONENT_RULE_SCHEDULE_MANAGEABLE = "schedule_manageable";

    /**
     * 组件规则：可时间管理
     */
    public static final String COMPONENT_RULE_TIME_MANAGEABLE = "time_manageable";

    /**
     * 组件规则：可定时管理
     */
    public static final String COMPONENT_RULE_TIMER_MANAGEABLE = "timer_manageable";

    /**
     * 组件规则：可延时管理
     */
    public static final String COMPONENT_RULE_DELAY_MANAGEABLE = "delay_manageable";

    /**
     * 组件规则：可异步管理
     */
    public static final String COMPONENT_RULE_ASYNC_MANAGEABLE = "async_manageable";

    /**
     * 组件规则：可同步管理
     */
    public static final String COMPONENT_RULE_SYNC_MANAGEABLE = "sync_manageable";

    /**
     * 组件规则：可并发管理
     */
    public static final String COMPONENT_RULE_CONCURRENT_MANAGEABLE = "concurrent_manageable";

    /**
     * 组件规则：可并行管理
     */
    public static final String COMPONENT_RULE_PARALLEL_MANAGEABLE = "parallel_manageable";

    /**
     * 组件规则：可串行管理
     */
    public static final String COMPONENT_RULE_SERIAL_MANAGEABLE = "serial_manageable";

    /**
     * 组件规则：可队列管理
     */
    public static final String COMPONENT_RULE_QUEUE_MANAGEABLE = "queue_manageable";

    /**
     * 组件规则：可栈管理
     */
    public static final String COMPONENT_RULE_STACK_MANAGEABLE = "stack_manageable";

    /**
     * 组件规则：可缓冲管理
     */
    public static final String COMPONENT_RULE_BUFFER_MANAGEABLE = "buffer_manageable";

    /**
     * 组件规则：可流管理
     */
    public static final String COMPONENT_RULE_STREAM_MANAGEABLE = "stream_manageable";

    /**
     * 组件规则：可管道管理
     */
    public static final String COMPONENT_RULE_PIPELINE_MANAGEABLE = "pipeline_manageable";

    /**
     * 组件规则：可通道管理
     */
    public static final String COMPONENT_RULE_CHANNEL_MANAGEABLE = "channel_manageable";

    /**
     * 组件规则：可会话管理
     */
    public static final String COMPONENT_RULE_SESSION_MANAGEABLE = "session_manageable";

    /**
     * 组件规则：可连接管理
     */
    public static final String COMPONENT_RULE_CONNECTION_MANAGEABLE = "connection_manageable";

    /**
     * 组件规则：可事务管理
     */
    public static final String COMPONENT_RULE_TRANSACTION_MANAGEABLE = "transaction_manageable";

    /**
     * 组件规则：可锁管理
     */
    public static final String COMPONENT_RULE_LOCK_MANAGEABLE = "lock_manageable";

    /**
     * 组件规则：可信号量管理
     */
    public static final String COMPONENT_RULE_SEMAPHORE_MANAGEABLE = "semaphore_manageable";

    /**
     * 组件规则：可屏障管理
     */
    public static final String COMPONENT_RULE_BARRIER_MANAGEABLE = "barrier_manageable";

    /**
     * 组件规则：可计数器管理
     */
    public static final String COMPONENT_RULE_COUNTER_MANAGEABLE = "counter_manageable";

    /**
     * 组件规则：可度量管理
     */
    public static final String COMPONENT_RULE_METRIC_MANAGEABLE = "metric_manageable";

    /**
     * 组件规则：可指标管理
     */
    public static final String COMPONENT_RULE_INDICATOR_MANAGEABLE = "indicator_manageable";

    /**
     * 组件规则：可统计管理
     */
    public static final String COMPONENT_RULE_STATISTIC_MANAGEABLE = "statistic_manageable";

    /**
     * 组件规则：可报告管理
     */
    public static final String COMPONENT_RULE_REPORT_MANAGEABLE = "report_manageable";

    /**
     * 组件规则：可日志管理
     */
    public static final String COMPONENT_RULE_LOG_MANAGEABLE = "log_manageable";

    /**
     * 组件规则：可跟踪管理
     */
    public static final String COMPONENT_RULE_TRACE_MANAGEABLE = "trace_manageable";

    /**
     * 组件规则：可调试管理
     */
    public static final String COMPONENT_RULE_DEBUG_MANAGEABLE = "debug_manageable";

    /**
     * 组件规则：可性能管理
     */
    public static final String COMPONENT_RULE_PERFORMANCE_MANAGEABLE = "performance_manageable";

    /**
     * 组件规则：可优化管理
     */
    public static final String COMPONENT_RULE_OPTIMIZATION_MANAGEABLE = "optimization_manageable";

    /**
     * 组件规则：可调优管理
     */
    public static final String COMPONENT_RULE_TUNING_MANAGEABLE = "tuning_manageable";

    /**
     * 组件规则：可基准测试管理
     */
    public static final String COMPONENT_RULE_BENCHMARK_MANAGEABLE = "benchmark_manageable";

    /**
     * 组件规则：可压力测试管理
     */
    public static final String COMPONENT_RULE_STRESS_TEST_MANAGEABLE = "stress_test_manageable";

    /**
     * 组件规则：可负载测试管理
     */
    public static final String COMPONENT_RULE_LOAD_TEST_MANAGEABLE = "load_test_manageable";

    /**
     * 组件规则：可单元测试管理
     */
    public static final String COMPONENT_RULE_UNIT_TEST_MANAGEABLE = "unit_test_manageable";

    /**
     * 组件规则：可集成测试管理
     */
    public static final String COMPONENT_RULE_INTEGRATION_TEST_MANAGEABLE = "integration_test_manageable";

    /**
     * 组件规则：可端到端测试管理
     */
    public static final String COMPONENT_RULE_E2E_TEST_MANAGEABLE = "e2e_test_manageable";

    /**
     * 组件规则：可回归测试管理
     */
    public static final String COMPONENT_RULE_REGRESSION_TEST_MANAGEABLE = "regression_test_manageable";

    /**
     * 组件规则：可冒烟测试管理
     */
    public static final String COMPONENT_RULE_SMOKE_TEST_MANAGEABLE = "smoke_test_manageable";

    /**
     * 组件规则：可验收测试管理
     */
    public static final String COMPONENT_RULE_ACCEPTANCE_TEST_MANAGEABLE = "acceptance_test_manageable";

    /**
     * 组件规则：可用户验收测试管理
     */
    public static final String COMPONENT_RULE_UAT_MANAGEABLE = "uat_manageable";

    /**
     * 组件规则：可A/B测试管理
     */
    public static final String COMPONENT_RULE_AB_TEST_MANAGEABLE = "ab_test_manageable";

    /**
     * 组件规则：可灰度测试管理
     */
    public static final String COMPONENT_RULE_CANARY_TEST_MANAGEABLE = "canary_test_manageable";

    /**
     * 组件规则：可蓝绿部署管理
     */
    public static final String COMPONENT_RULE_BLUE_GREEN_DEPLOYMENT_MANAGEABLE = "blue_green_deployment_manageable";

    /**
     * 组件规则：可滚动部署管理
     */
    public static final String COMPONENT_RULE_ROLLING_DEPLOYMENT_MANAGEABLE = "rolling_deployment_manageable";

    /**
     * 组件规则：可金丝雀部署管理
     */
    public static final String COMPONENT_RULE_CANARY_DEPLOYMENT_MANAGEABLE = "canary_deployment_manageable";

    /**
     * 组件规则：可影子部署管理
     */
    public static final String COMPONENT_RULE_SHADOW_DEPLOYMENT_MANAGEABLE = "shadow_deployment_manageable";

    /**
     * 组件规则：可特性开关管理
     */
    public static final String COMPONENT_RULE_FEATURE_TOGGLE_MANAGEABLE = "feature_toggle_manageable";

    /**
     * 组件规则：可配置中心管理
     */
    public static final String COMPONENT_RULE_CONFIG_CENTER_MANAGEABLE = "config_center_manageable";

    /**
     * 组件规则：可服务发现管理
     */
    public static final String COMPONENT_RULE_SERVICE_DISCOVERY_MANAGEABLE = "service_discovery_manageable";

    /**
     * 组件规则：可服务注册管理
     */
    public static final String COMPONENT_RULE_SERVICE_REGISTRY_MANAGEABLE = "service_registry_manageable";

    /**
     * 组件规则：可服务网格管理
     */
    public static final String COMPONENT_RULE_SERVICE_MESH_MANAGEABLE = "service_mesh_manageable";

    /**
     * 组件规则：可API网关管理
     */
    public static final String COMPONENT_RULE_API_GATEWAY_MANAGEABLE = "api_gateway_manageable";

    /**
     * 组件规则：可反向代理管理
     */
    public static final String COMPONENT_RULE_REVERSE_PROXY_MANAGEABLE = "reverse_proxy_manageable";

    /**
     * 组件规则：可正向代理管理
     */
    public static final String COMPONENT_RULE_FORWARD_PROXY_MANAGEABLE = "forward_proxy_manageable";

    /**
     * 组件规则：可CDN管理
     */
    public static final String COMPONENT_RULE_CDN_MANAGEABLE = "cdn_manageable";

    /**
     * 组件规则：可DNS管理
     */
    public static final String COMPONENT_RULE_DNS_MANAGEABLE = "dns_manageable";

    /**
     * 组件规则：可SSL/TLS管理
     */
    public static final String COMPONENT_RULE_SSL_TLS_MANAGEABLE = "ssl_tls_manageable";

    /**
     * 组件规则：可证书管理
     */
    public static final String COMPONENT_RULE_CERTIFICATE_MANAGEABLE = "certificate_manageable";

    /**
     * 组件规则：可密钥管理
     */
    public static final String COMPONENT_RULE_KEY_MANAGEABLE = "key_manageable";

    /**
     * 组件规则：可密码管理
     */
    public static final String COMPONENT_RULE_PASSWORD_MANAGEABLE = "password_manageable";

    /**
     * 组件规则：可令牌管理
     */
    public static final String COMPONENT_RULE_TOKEN_MANAGEABLE = "token_manageable";

    /**
     * 组件规则：可会话管理
     */
    public static final String COMPONENT_RULE_SESSION_TOKEN_MANAGEABLE = "session_token_manageable";

    /**
     * 组件规则：可刷新令牌管理
     */
    public static final String COMPONENT_RULE_REFRESH_TOKEN_MANAGEABLE = "refresh_token_manageable";

    /**
     * 组件规则：可访问令牌管理
     */
    public static final String COMPONENT_RULE_ACCESS_TOKEN_MANAGEABLE = "access_token_manageable";

    /**
     * 组件规则：可身份令牌管理
     */
    public static final String COMPONENT_RULE_ID_TOKEN_MANAGEABLE = "id_token_manageable";

    /**
     * 组件规则：可JWT管理
     */
    public static final String COMPONENT_RULE_JWT_MANAGEABLE = "jwt_manageable";

    /**
     * 组件规则：可OAuth管理
     */
    public static final String COMPONENT_RULE_OAUTH_MANAGEABLE = "oauth_manageable";

    /**
     * 组件规则：可OpenID Connect管理
     */
    public static final String COMPONENT_RULE_OIDC_MANAGEABLE = "oidc_manageable";

    /**
     * 组件规则：可SAML管理
     */
    public static final String COMPONENT_RULE_SAML_MANAGEABLE = "saml_manageable";

    /**
     * 组件规则：可LDAP管理
     */
    public static final String COMPONENT_RULE_LDAP_MANAGEABLE = "ldap_manageable";

    /**
     * 组件规则：可Active Directory管理
     */
    public static final String COMPONENT_RULE_AD_MANAGEABLE = "ad_manageable";

    /**
     * 组件规则：可单点登录管理
     */
    public static final String COMPONENT_RULE_SSO_MANAGEABLE = "sso_manageable";

    /**
     * 组件规则：可多因子认证管理
     */
    public static final String COMPONENT_RULE_MFA_MANAGEABLE = "mfa_manageable";

    /**
     * 组件规则：可双因子认证管理
     */
    public static final String COMPONENT_RULE_2FA_MANAGEABLE = "2fa_manageable";

    /**
     * 组件规则：可生物识别管理
     */
    public static final String COMPONENT_RULE_BIOMETRIC_MANAGEABLE = "biometric_manageable";

    /**
     * 组件规则：可指纹识别管理
     */
    public static final String COMPONENT_RULE_FINGERPRINT_MANAGEABLE = "fingerprint_manageable";

    /**
     * 组件规则：可面部识别管理
     */
    public static final String COMPONENT_RULE_FACE_RECOGNITION_MANAGEABLE = "face_recognition_manageable";

    /**
     * 组件规则：可语音识别管理
     */
    public static final String COMPONENT_RULE_VOICE_RECOGNITION_MANAGEABLE = "voice_recognition_manageable";

    /**
     * 组件规则：可虹膜识别管理
     */
    public static final String COMPONENT_RULE_IRIS_RECOGNITION_MANAGEABLE = "iris_recognition_manageable";

    /**
     * 组件规则：可手势识别管理
     */
    public static final String COMPONENT_RULE_GESTURE_RECOGNITION_MANAGEABLE = "gesture_recognition_manageable";

    /**
     * 组件规则：可行为识别管理
     */
    public static final String COMPONENT_RULE_BEHAVIOR_RECOGNITION_MANAGEABLE = "behavior_recognition_manageable";

    /**
     * 组件规则：可模式识别管理
     */
    public static final String COMPONENT_RULE_PATTERN_RECOGNITION_MANAGEABLE = "pattern_recognition_manageable";

    /**
     * 组件规则：可异常检测管理
     */
    public static final String COMPONENT_RULE_ANOMALY_DETECTION_MANAGEABLE = "anomaly_detection_manageable";

    /**
     * 组件规则：可入侵检测管理
     */
    public static final String COMPONENT_RULE_INTRUSION_DETECTION_MANAGEABLE = "intrusion_detection_manageable";

    /**
     * 组件规则：可威胁检测管理
     */
    public static final String COMPONENT_RULE_THREAT_DETECTION_MANAGEABLE = "threat_detection_manageable";

    /**
     * 组件规则：可恶意软件检测管理
     */
    public static final String COMPONENT_RULE_MALWARE_DETECTION_MANAGEABLE = "malware_detection_manageable";

    /**
     * 组件规则：可病毒检测管理
     */
    public static final String COMPONENT_RULE_VIRUS_DETECTION_MANAGEABLE = "virus_detection_manageable";

    /**
     * 组件规则：可木马检测管理
     */
    public static final String COMPONENT_RULE_TROJAN_DETECTION_MANAGEABLE = "trojan_detection_manageable";

    /**
     * 组件规则：可间谍软件检测管理
     */
    public static final String COMPONENT_RULE_SPYWARE_DETECTION_MANAGEABLE = "spyware_detection_manageable";

    /**
     * 组件规则：可广告软件检测管理
     */
    public static final String COMPONENT_RULE_ADWARE_DETECTION_MANAGEABLE = "adware_detection_manageable";

    /**
     * 组件规则：可勒索软件检测管理
     */
    public static final String COMPONENT_RULE_RANSOMWARE_DETECTION_MANAGEABLE = "ransomware_detection_manageable";

    /**
     * 组件规则：可钓鱼检测管理
     */
    public static final String COMPONENT_RULE_PHISHING_DETECTION_MANAGEABLE = "phishing_detection_manageable";

    /**
     * 组件规则：可社会工程学检测管理
     */
    public static final String COMPONENT_RULE_SOCIAL_ENGINEERING_DETECTION_MANAGEABLE = "social_engineering_detection_manageable";

    /**
     * 组件规则：可DDoS检测管理
     */
    public static final String COMPONENT_RULE_DDOS_DETECTION_MANAGEABLE = "ddos_detection_manageable";

    /**
     * 组件规则：可SQL注入检测管理
     */
    public static final String COMPONENT_RULE_SQL_INJECTION_DETECTION_MANAGEABLE = "sql_injection_detection_manageable";

    /**
     * 组件规则：可XSS检测管理
     */
    public static final String COMPONENT_RULE_XSS_DETECTION_MANAGEABLE = "xss_detection_manageable";

    /**
     * 组件规则：可CSRF检测管理
     */
    public static final String COMPONENT_RULE_CSRF_DETECTION_MANAGEABLE = "csrf_detection_manageable";

    /**
     * 组件规则：可SSRF检测管理
     */
    public static final String COMPONENT_RULE_SSRF_DETECTION_MANAGEABLE = "ssrf_detection_manageable";

    /**
     * 组件规则：可XXE检测管理
     */
    public static final String COMPONENT_RULE_XXE_DETECTION_MANAGEABLE = "xxe_detection_manageable";

    /**
     * 组件规则：可路径遍历检测管理
     */
    public static final String COMPONENT_RULE_PATH_TRAVERSAL_DETECTION_MANAGEABLE = "path_traversal_detection_manageable";

    /**
     * 组件规则：可命令注入检测管理
     */
    public static final String COMPONENT_RULE_COMMAND_INJECTION_DETECTION_MANAGEABLE = "command_injection_detection_manageable";

    /**
     * 组件规则：可代码注入检测管理
     */
    public static final String COMPONENT_RULE_CODE_INJECTION_DETECTION_MANAGEABLE = "code_injection_detection_manageable";

    /**
     * 组件规则：可LDAP注入检测管理
     */
    public static final String COMPONENT_RULE_LDAP_INJECTION_DETECTION_MANAGEABLE = "ldap_injection_detection_manageable";

    /**
     * 组件规则：可XPath注入检测管理
     */
    public static final String COMPONENT_RULE_XPATH_INJECTION_DETECTION_MANAGEABLE = "xpath_injection_detection_manageable";

    /**
     * 组件规则：可NoSQL注入检测管理
     */
    public static final String COMPONENT_RULE_NOSQL_INJECTION_DETECTION_MANAGEABLE = "nosql_injection_detection_manageable";

    /**
     * 组件规则：可HTTP头注入检测管理
     */
    public static final String COMPONENT_RULE_HTTP_HEADER_INJECTION_DETECTION_MANAGEABLE = "http_header_injection_detection_manageable";

    /**
     * 组件规则：可HTTP响应分割检测管理
     */
    public static final String COMPONENT_RULE_HTTP_RESPONSE_SPLITTING_DETECTION_MANAGEABLE = "http_response_splitting_detection_manageable";

    /**
     * 组件规则：可会话固定检测管理
     */
    public static final String COMPONENT_RULE_SESSION_FIXATION_DETECTION_MANAGEABLE = "session_fixation_detection_manageable";

    /**
     * 组件规则：可会话劫持检测管理
     */
    public static final String COMPONENT_RULE_SESSION_HIJACKING_DETECTION_MANAGEABLE = "session_hijacking_detection_manageable";

    /**
     * 组件规则：可点击劫持检测管理
     */
    public static final String COMPONENT_RULE_CLICKJACKING_DETECTION_MANAGEABLE = "clickjacking_detection_manageable";

    /**
     * 组件规则：可UI重绘检测管理
     */
    public static final String COMPONENT_RULE_UI_REDRESSING_DETECTION_MANAGEABLE = "ui_redressing_detection_manageable";

    /**
     * 组件规则：可缓冲区溢出检测管理
     */
    public static final String COMPONENT_RULE_BUFFER_OVERFLOW_DETECTION_MANAGEABLE = "buffer_overflow_detection_manageable";

    /**
     * 组件规则：可整数溢出检测管理
     */
    public static final String COMPONENT_RULE_INTEGER_OVERFLOW_DETECTION_MANAGEABLE = "integer_overflow_detection_manageable";

    /**
     * 组件规则：可格式字符串漏洞检测管理
     */
    public static final String COMPONENT_RULE_FORMAT_STRING_VULNERABILITY_DETECTION_MANAGEABLE = "format_string_vulnerability_detection_manageable";

    /**
     * 组件规则：可竞态条件检测管理
     */
    public static final String COMPONENT_RULE_RACE_CONDITION_DETECTION_MANAGEABLE = "race_condition_detection_manageable";

    /**
     * 组件规则：可时间检查时间使用检测管理
     */
    public static final String COMPONENT_RULE_TOCTOU_DETECTION_MANAGEABLE = "toctou_detection_manageable";

    /**
     * 组件规则：可权限提升检测管理
     */
    public static final String COMPONENT_RULE_PRIVILEGE_ESCALATION_DETECTION_MANAGEABLE = "privilege_escalation_detection_manageable";

    /**
     * 组件规则：可信息泄露检测管理
     */
    public static final String COMPONENT_RULE_INFORMATION_DISCLOSURE_DETECTION_MANAGEABLE = "information_disclosure_detection_manageable";

    /**
     * 组件规则：可拒绝服务检测管理
     */
    public static final String COMPONENT_RULE_DENIAL_OF_SERVICE_DETECTION_MANAGEABLE = "denial_of_service_detection_manageable";

    /**
     * 组件规则：可资源耗尽检测管理
     */
    public static final String COMPONENT_RULE_RESOURCE_EXHAUSTION_DETECTION_MANAGEABLE = "resource_exhaustion_detection_manageable";

    /**
     * 组件规则：可内存泄漏检测管理
     */
    public static final String COMPONENT_RULE_MEMORY_LEAK_DETECTION_MANAGEABLE = "memory_leak_detection_manageable";

    /**
     * 组件规则：可死锁检测管理
     */
    public static final String COMPONENT_RULE_DEADLOCK_DETECTION_MANAGEABLE = "deadlock_detection_manageable";

    /**
     * 组件规则：可活锁检测管理
     */
    public static final String COMPONENT_RULE_LIVELOCK_DETECTION_MANAGEABLE = "livelock_detection_manageable";

    /**
     * 组件规则：可饥饿检测管理
     */
    public static final String COMPONENT_RULE_STARVATION_DETECTION_MANAGEABLE = "starvation_detection_manageable";

    /**
     * 组件规则：可优先级反转检测管理
     */
    public static final String COMPONENT_RULE_PRIORITY_INVERSION_DETECTION_MANAGEABLE = "priority_inversion_detection_manageable";

    /**
     * 组件规则：可数据竞争检测管理
     */
    public static final String COMPONENT_RULE_DATA_RACE_DETECTION_MANAGEABLE = "data_race_detection_manageable";

    /**
     * 组件规则：可原子性违反检测管理
     */
    public static final String COMPONENT_RULE_ATOMICITY_VIOLATION_DETECTION_MANAGEABLE = "atomicity_violation_detection_manageable";

    /**
     * 组件规则：可顺序违反检测管理
     */
    public static final String COMPONENT_RULE_ORDER_VIOLATION_DETECTION_MANAGEABLE = "order_violation_detection_manageable";

    /**
     * 组件规则：可一致性违反检测管理
     */
    public static final String COMPONENT_RULE_CONSISTENCY_VIOLATION_DETECTION_MANAGEABLE = "consistency_violation_detection_manageable";

    /**
     * 组件规则：可隔离性违反检测管理
     */
    public static final String COMPONENT_RULE_ISOLATION_VIOLATION_DETECTION_MANAGEABLE = "isolation_violation_detection_manageable";

    /**
     * 组件规则：可持久性违反检测管理
     */
    public static final String COMPONENT_RULE_DURABILITY_VIOLATION_DETECTION_MANAGEABLE = "durability_violation_detection_manageable";

    /**
     * 组件规则：可ACID违反检测管理
     */
    public static final String COMPONENT_RULE_ACID_VIOLATION_DETECTION_MANAGEABLE = "acid_violation_detection_manageable";

    /**
     * 组件规则：可BASE违反检测管理
     */
    public static final String COMPONENT_RULE_BASE_VIOLATION_DETECTION_MANAGEABLE = "base_violation_detection_manageable";

    /**
     * 组件规则：可CAP定理违反检测管理
     */
    public static final String COMPONENT_RULE_CAP_THEOREM_VIOLATION_DETECTION_MANAGEABLE = "cap_theorem_violation_detection_manageable";

    /**
     * 组件规则：可分区容错检测管理
     */
    public static final String COMPONENT_RULE_PARTITION_TOLERANCE_DETECTION_MANAGEABLE = "partition_tolerance_detection_manageable";

    /**
     * 组件规则：可网络分区检测管理
     */
    public static final String COMPONENT_RULE_NETWORK_PARTITION_DETECTION_MANAGEABLE = "network_partition_detection_manageable";

    /**
     * 组件规则：可脑裂检测管理
     */
    public static final String COMPONENT_RULE_SPLIT_BRAIN_DETECTION_MANAGEABLE = "split_brain_detection_manageable";

    /**
     * 组件规则：可拜占庭故障检测管理
     */
    public static final String COMPONENT_RULE_BYZANTINE_FAULT_DETECTION_MANAGEABLE = "byzantine_fault_detection_manageable";

    /**
     * 组件规则：可故障注入管理
     */
    public static final String COMPONENT_RULE_FAULT_INJECTION_MANAGEABLE = "fault_injection_manageable";

    /**
     * 组件规则：可混沌工程管理
     */
    public static final String COMPONENT_RULE_CHAOS_ENGINEERING_MANAGEABLE = "chaos_engineering_manageable";

    /**
     * 组件规则：可可靠性工程管理
     */
    public static final String COMPONENT_RULE_RELIABILITY_ENGINEERING_MANAGEABLE = "reliability_engineering_manageable";

    /**
     * 组件规则：可站点可靠性工程管理
     */
    public static final String COMPONENT_RULE_SRE_MANAGEABLE = "sre_manageable";

    /**
     * 组件规则：可DevOps管理
     */
    public static final String COMPONENT_RULE_DEVOPS_MANAGEABLE = "devops_manageable";

    /**
     * 组件规则：可持续集成管理
     */
    public static final String COMPONENT_RULE_CI_MANAGEABLE = "ci_manageable";

    /**
     * 组件规则：可持续部署管理
     */
    public static final String COMPONENT_RULE_CD_MANAGEABLE = "cd_manageable";

    /**
     * 组件规则：可持续交付管理
     */
    public static final String COMPONENT_RULE_CONTINUOUS_DELIVERY_MANAGEABLE = "continuous_delivery_manageable";

    /**
     * 组件规则：可基础设施即代码管理
     */
    public static final String COMPONENT_RULE_IAC_MANAGEABLE = "iac_manageable";

    /**
     * 组件规则：可配置即代码管理
     */
    public static final String COMPONENT_RULE_CAC_MANAGEABLE = "cac_manageable";

    /**
     * 组件规则：可策略即代码管理
     */
    public static final String COMPONENT_RULE_PAC_MANAGEABLE = "pac_manageable";

    /**
     * 组件规则：可安全即代码管理
     */
    public static final String COMPONENT_RULE_SAC_MANAGEABLE = "sac_manageable";

    /**
     * 组件规则：可合规即代码管理
     */
    public static final String COMPONENT_RULE_COMPLIANCE_AS_CODE_MANAGEABLE = "compliance_as_code_manageable";

    /**
     * 组件规则：可治理即代码管理
     */
    public static final String COMPONENT_RULE_GOVERNANCE_AS_CODE_MANAGEABLE = "governance_as_code_manageable";

    /**
     * 组件规则：可文档即代码管理
     */
    public static final String COMPONENT_RULE_DOCS_AS_CODE_MANAGEABLE = "docs_as_code_manageable";

    /**
     * 组件规则：可测试即代码管理
     */
    public static final String COMPONENT_RULE_TESTS_AS_CODE_MANAGEABLE = "tests_as_code_manageable";

    /**
     * 组件规则：可监控即代码管理
     */
    public static final String COMPONENT_RULE_MONITORING_AS_CODE_MANAGEABLE = "monitoring_as_code_manageable";

    /**
     * 组件规则：可告警即代码管理
     */
    public static final String COMPONENT_RULE_ALERTING_AS_CODE_MANAGEABLE = "alerting_as_code_manageable";

    /**
     * 组件规则：可日志即代码管理
     */
    public static final String COMPONENT_RULE_LOGGING_AS_CODE_MANAGEABLE = "logging_as_code_manageable";

    /**
     * 组件规则：可度量即代码管理
     */
    public static final String COMPONENT_RULE_METRICS_AS_CODE_MANAGEABLE = "metrics_as_code_manageable";

    /**
     * 组件规则：可跟踪即代码管理
     */
    public static final String COMPONENT_RULE_TRACING_AS_CODE_MANAGEABLE = "tracing_as_code_manageable";

    /**
     * 组件规则：可可观测性即代码管理
     */
    public static final String COMPONENT_RULE_OBSERVABILITY_AS_CODE_MANAGEABLE = "observability_as_code_manageable";

    /**
     * 组件规则：可GitOps管理
     */
    public static final String COMPONENT_RULE_GITOPS_MANAGEABLE = "gitops_manageable";

    /**
     * 组件规则：可ChatOps管理
     */
    public static final String COMPONENT_RULE_CHATOPS_MANAGEABLE = "chatops_manageable";

    /**
     * 组件规则：可NoOps管理
     */
    public static final String COMPONENT_RULE_NOOPS_MANAGEABLE = "noops_manageable";

    /**
     * 组件规则：可AIOps管理
     */
    public static final String COMPONENT_RULE_AIOPS_MANAGEABLE = "aiops_manageable";

    /**
     * 组件规则：可MLOps管理
     */
    public static final String COMPONENT_RULE_MLOPS_MANAGEABLE = "mlops_manageable";

    /**
     * 组件规则：可DataOps管理
     */
    public static final String COMPONENT_RULE_DATAOPS_MANAGEABLE = "dataops_manageable";

    /**
     * 组件规则：可SecOps管理
     */
    public static final String COMPONENT_RULE_SECOPS_MANAGEABLE = "secops_manageable";

    /**
     * 组件规则：可DevSecOps管理
     */
    public static final String COMPONENT_RULE_DEVSECOPS_MANAGEABLE = "devsecops_manageable";

    /**
     * 组件规则：可FinOps管理
     */
    public static final String COMPONENT_RULE_FINOPS_MANAGEABLE = "finops_manageable";

}