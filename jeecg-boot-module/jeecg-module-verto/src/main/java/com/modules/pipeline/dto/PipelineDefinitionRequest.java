package com.verto.modules.pipeline.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 统一接收前端完整流水线配置（阶段、触发器、变量等），由后端生成 Jenkinsfile 并创建/更新 Jenkins Job
 */
@Data
public class PipelineDefinitionRequest {
    /** Jenkins 作业名称（唯一） */
    private String jobName;

    /** Agent 标签（可选，未填则使用 any） */
    private String agentLabel;

    /** 全局环境变量 */
    private Map<String, String> variables;

    /** 触发器配置 */
    private Triggers triggers;

    /** 阶段配置 */
    private List<Stage> stages;

    @Data
    public static class Stage {
        /** 阶段英文名 */
        private String name;
        /** 阶段展示名 */
        private String displayName;
        /** 超时（秒，可选） */
        private Integer timeoutSeconds;
        /** 执行命令（steps） */
        private List<String> commands;
    }

    @Data
    public static class Triggers {
        /** cron 表达式（可选） */
        private String cron;
        /** 是否启用轮询SCM（可选） */
        private Boolean pollScm;
    }
}