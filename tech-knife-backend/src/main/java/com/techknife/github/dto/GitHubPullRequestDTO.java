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
public class GitHubPullRequestDTO {
    private String id;
    private String repositoryId;
    private int prNumber;
    private String title;
    private String description;
    private String status;
    private String headBranch;
    private String baseBranch;
    private String authorUsername;
    private List<String> reviewerUsernames;
    private String reviewStatus;
    private String mergedBy;
    private Instant mergeDate;
    private Instant closedAt;
    private String htmlUrl;
    private String linkedTaskId;
    private Instant createdAt;
    private Instant updatedAt;
}
