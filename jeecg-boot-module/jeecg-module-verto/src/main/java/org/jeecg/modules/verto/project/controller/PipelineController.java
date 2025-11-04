package org.jeecg.modules.verto.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.verto.project.dto.BuildRecordDTO;
import org.jeecg.modules.verto.project.dto.PipelineDTO;
import org.jeecg.modules.verto.project.entity.VertoPipeline;
import org.jeecg.modules.verto.project.entity.VertoProjectPipeline;
import org.jeecg.modules.verto.project.mapper.VertoPipelineMapper;
import org.jeecg.modules.verto.project.mapper.VertoProjectPipelineMapper;
import org.jeecg.modules.verto.project.service.IVertoPipelineService;
import org.jeecg.modules.verto.appmanage.util.Jsons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/jeecgboot/verto/pipeline", "/verto-backend/pipeline"})
public class PipelineController {

    @Autowired
    private IVertoPipelineService pipelineService;
    @Autowired
    private VertoPipelineMapper pipelineMapper;
    @Autowired
    private VertoProjectPipelineMapper projectPipelineMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam String applicationId) {
        List<VertoPipeline> list = pipelineService.list(new QueryWrapper<VertoPipeline>().lambda()
                .eq(VertoPipeline::getApplicationId, applicationId));
        List<PipelineDTO> dtos = list.stream().map(this::toDTO).collect(Collectors.toList());
        return Result.OK(dtos);
    }

    @PostMapping("/create")
    public Result<?> create(@RequestBody Map<String, Object> payload) {
        VertoPipeline p = new VertoPipeline();
        p.setApplicationId((String) payload.get("applicationId"));
        p.setPipelineName((String) payload.getOrDefault("name", "pipeline"));
        p.setStatus(((Boolean) payload.getOrDefault("enabled", true)) ? "enabled" : "disabled");
        // store environments/autoTrigger in config JSON
        p.setConfig(Jsons.toJsonString(payload));
        p.setCreateTime(new Date());
        p.setUpdateTime(new Date());
        pipelineService.save(p);
        return Result.OK(toDTO(p));
    }

    @PostMapping("/run")
    public Result<?> run(@RequestBody Map<String, Object> payload) {
        String pipelineId = (String) payload.get("pipelineId");
        VertoPipeline pipeline = pipelineService.getById(pipelineId);
        if (pipeline == null) {
            return Result.error("Pipeline not found");
        }
        int nextNo = projectPipelineMapper.getMaxBuildNumber(pipelineId) + 1;
        VertoProjectPipeline record = new VertoProjectPipeline();
        record.setPipelineId(pipelineId);
        record.setProjectId((String) payload.getOrDefault("projectId", "mock-project"));
        record.setBuildNumber(nextNo);
        record.setStatus("running");
        record.setGitBranch((String) payload.getOrDefault("branch", "main"));
        record.setGitCommit((String) payload.getOrDefault("commitId", ""));
        record.setConfig(Jsons.toJsonString(payload));
        record.setLogs(null);
        record.setStartTime(new Date());
        record.setDuration(0L);
        projectPipelineMapper.insert(record);
        return Result.OK(toBuildDTO(record));
    }

    @GetMapping("/history")
    public Result<?> history(@RequestParam String pipelineId) {
        List<VertoProjectPipeline> list = projectPipelineMapper.selectList(new QueryWrapper<VertoProjectPipeline>().lambda()
                .eq(VertoProjectPipeline::getPipelineId, pipelineId)
                .orderByDesc(VertoProjectPipeline::getBuildNumber));
        List<BuildRecordDTO> dtos = list.stream().map(this::toBuildDTO).collect(Collectors.toList());
        return Result.OK(dtos);
    }

    @GetMapping("/build/detail")
    public Result<?> buildDetail(@RequestParam String id) {
        VertoProjectPipeline rec = projectPipelineMapper.selectById(id);
        if (rec == null) return Result.error("Build not found");
        return Result.OK(toBuildDTO(rec));
    }

    @GetMapping("/build/logs")
    public Result<?> buildLogs(@RequestParam String id) {
        VertoProjectPipeline rec = projectPipelineMapper.selectById(id);
        if (rec == null) return Result.error("Build not found");
        return Result.OK(Map.of("logs", rec.getLogs() == null ? "" : rec.getLogs()));
    }

    private PipelineDTO toDTO(VertoPipeline p) {
        PipelineDTO dto = new PipelineDTO();
        dto.setId(p.getId());
        dto.setApplicationId(p.getApplicationId());
        dto.setName(p.getPipelineName());
        dto.setDescription(Jsons.getString(p.getConfig(), "description", null));
        dto.setEnabled("enabled".equalsIgnoreCase(p.getStatus()));
        dto.setAutoTrigger(Jsons.getBoolean(p.getConfig(), "autoTrigger", false));
        dto.setEnvironments(Jsons.getString(p.getConfig(), "environments", null));
        dto.setCreateTime(formatDate(p.getCreateTime()));
        dto.setUpdateTime(formatDate(p.getUpdateTime()));
        return dto;
    }

    private BuildRecordDTO toBuildDTO(VertoProjectPipeline rec) {
        BuildRecordDTO dto = new BuildRecordDTO();
        dto.setId(rec.getId());
        dto.setPipelineId(rec.getPipelineId());
        dto.setNumber(rec.getBuildNumber());
        dto.setStatus(rec.getStatus());
        dto.setStartTime(formatDate(rec.getStartTime()));
        dto.setEndTime(formatDate(rec.getEndTime()));
        dto.setDuration(rec.getDuration());
        dto.setBranch(rec.getGitBranch());
        dto.setCommitId(rec.getGitCommit());
        dto.setCommitMessage(Jsons.getString(rec.getConfig(), "commitMessage", null));
        dto.setAuthor(Jsons.getString(rec.getConfig(), "author", null));
        dto.setCurrentStage(Jsons.getString(rec.getConfig(), "currentStage", null));
        dto.setProgress(Jsons.getInteger(rec.getConfig(), "progress", 0));
        return dto;
    }

    private String formatDate(Date d) {
        if (d == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
    }
}