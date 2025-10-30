package com.verto.modules.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GitRepoCreateRequest", description = "创建Git仓库请求参数")
public class GitRepoCreateRequest {
    @Schema(description = "Git仓库地址（包含owner和repo名称）")
    private String gitUrl;

    @Schema(description = "访问令牌（Github PAT或OAuth令牌）")
    private String token;

    @Schema(description = "仓库可见性：private/public")
    private String visibility;

    public String getGitUrl() {
        return gitUrl;
    }

    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}