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
public class GitHubIssueDTO {
    private String id;
    private String repositoryId;
    private int issueNumber;
    private String title;
    private String body;
    private String assigneeUsername;
    private List<String> labels;
    private String milestoneTitle;
    private String priority;
    private String status;
    private String createdBy;
    private String htmlUrl;
    private String linkedTaskId;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
