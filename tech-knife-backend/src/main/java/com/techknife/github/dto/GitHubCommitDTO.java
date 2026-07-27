package com.techknife.github.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitHubCommitDTO {
    private String id;
    private String repositoryId;
    private String commitSha;
    private String authorName;
    private String authorEmail;
    private String authorUsername;
    private String committerName;
    private String committerEmail;
    private String message;
    private String branchName;
    private int filesChanged;
    private int additions;
    private int deletions;
    private Instant commitTime;
    private String linkedEmployeeId;
    private String htmlUrl;
}
