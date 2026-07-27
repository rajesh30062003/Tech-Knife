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
public class GitHubBranchDTO {
    private String id;
    private String repositoryId;
    private String name;
    private boolean isDefault;
    private boolean isProtected;
    private String creatorUsername;
    private String lastCommitSha;
    private String lastCommitMessage;
    private Instant lastCommitDate;
    private Instant updatedAt;
}
