package com.verto.modules.appmanage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Persisted Git repository details bound to an application.
 */
@Data
@TableName("app_git_repo_info")
public class AppGitRepoInfo {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /** Application ID this repo info is bound to */
    private String appId;

    /** Owner and repo name parsed from gitUrl */
    private String owner;
    private String repoName;

    /** URLs */
    private String htmlUrl;
    private String cloneUrl;
    private String sshUrl;

    /** Metadata */
    private String description;
    private String visibility; // public/private
    private Integer stars;
    private Integer forks;
    private Integer openIssues;
    private String license;
    private String topics; // comma-separated or JSON array string

    /** Branch/commit info */
    private String defaultBranch;
    private Integer branchCount;
    private String lastCommitSha;
    private String lastCommitMessage;
    private String lastCommitter;
    private LocalDateTime lastCommitTime;

    /** Sync timestamps */
    private LocalDateTime createdAt; // repo created time
    private LocalDateTime updatedAt; // repo updated time
    private LocalDateTime lastSyncedAt; // this record sync time
}