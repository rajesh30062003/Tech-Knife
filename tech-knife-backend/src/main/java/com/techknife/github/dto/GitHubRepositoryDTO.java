package com.techknife.github.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepositoryDTO {
    private String id;
    private String githubRepoId;
    private String repoName;
    private String fullName;
    private String owner;
    private String description;
    private String defaultBranch;
    private String visibility;
    private String language;
    private String license;
    private List<String> topics;
    private boolean archived;
    private boolean linked;
    private String linkedProjectId;
    private String cloneUrl;
    private String htmlUrl;
    private int starsCount;
    private int forksCount;
    private int openIssuesCount;
    private Instant lastSyncedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
