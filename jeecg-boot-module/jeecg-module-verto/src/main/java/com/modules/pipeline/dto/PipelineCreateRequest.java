package com.verto.modules.pipeline.dto;

import lombok.Data;

/**
 * 创建 Jenkins 流水线（Job）请求
 */
@Data
public class PipelineCreateRequest {
    /** 作业名称（唯一） */
    private String jobName;

    /** 是否使用SCM（Git）拉取 Jenkinsfile，否则使用内联脚本 */
    private boolean useScm;

    /** Git 仓库地址 */
    private String repoUrl;

    /** 分支名称（例如：main 或 master） */
    private String branch;

    /** Jenkins 凭据ID（可选） */
    private String credentialsId;

    /** Jenkinsfile 路径（使用SCM时） */
    private String jenkinsfilePath;

    /** 是否使用内联流水线脚本 */
    private boolean useInlineScript;

    /** 内联流水线脚本内容（使用 CpsFlowDefinition） */
    private String pipelineScript;
}