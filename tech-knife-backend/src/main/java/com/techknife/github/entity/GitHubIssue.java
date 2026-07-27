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
@Document(collection = "github_issues")
public class GitHubIssue {

    @Id
    private String id;

    private String repositoryId;

    private int issueNumber;

    private String title;

    private String body;

    private String assigneeUsername;

    @Builder.Default
    private List<String> labels = new ArrayList<>();

    private String milestoneTitle;

    private String priority;

    private String status; // OPEN / CLOSED

    private String createdBy;

    private String htmlUrl;

    private String linkedTaskId;

    private Instant closedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
