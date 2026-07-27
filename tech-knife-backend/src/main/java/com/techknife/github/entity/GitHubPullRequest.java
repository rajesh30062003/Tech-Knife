package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_pull_requests")
public class GitHubPullRequest {

    @Id
    private String id;

    private String repositoryId;

    private int prNumber;

    private String title;

    private String description;

    private String status; // OPEN / CLOSED / MERGED

    private String headBranch;

    private String baseBranch;

    private String authorUsername;

    @Builder.Default
    private List<String> reviewerUsernames = new ArrayList<>();

    private String reviewStatus; // APPROVED / CHANGES_REQUESTED / PENDING

    private String mergedBy;

    private Instant mergeDate;

    private Instant closedAt;

    private String htmlUrl;

    private String linkedTaskId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
