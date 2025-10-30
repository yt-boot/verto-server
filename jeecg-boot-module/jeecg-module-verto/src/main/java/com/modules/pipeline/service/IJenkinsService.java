package com.verto.modules.pipeline.service;

import com.verto.modules.pipeline.dto.PipelineCreateRequest;
import com.verto.modules.pipeline.dto.PipelineDefinitionRequest;

import java.util.Map;

/** Jenkins 相关服务 */
public interface IJenkinsService {
    /**
     * 创建或更新 Jenkins Pipeline Job
     * @param request 参数
     * @return 结果信息，包含 jobUrl、action 等字段；失败时包含 error
     */
    Map<String, Object> createOrUpdatePipelineJob(PipelineCreateRequest request);

    /**
     * 根据完整配置生成 Jenkinsfile（内联脚本）并创建或更新 Jenkins Pipeline Job
     * @param request 完整配置（阶段、触发器、变量等）
     * @return 结果信息，包含 jobUrl、action 等字段；失败时包含 error
     */
    Map<String, Object> createPipelineJobFromDefinition(PipelineDefinitionRequest request);

    /**
     * 触发 Jenkins 构建（支持参数化构建）
     * @param jobName Jenkins 作业名称
     * @param parameters 构建参数（例如 BRANCH、COMMIT_ID、VERSION 等）
     * @return 结果信息，包含 queueUrl、queueId、jobUrl 等字段；失败时包含 error
     */
    Map<String, Object> triggerBuild(String jobName, java.util.Map<String, String> parameters);

    // 新增：校验 Jenkins Job 是否存在
    boolean jobExists(String jobName);
}