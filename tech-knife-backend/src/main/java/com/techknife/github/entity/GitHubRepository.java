package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_repositories")
public class GitHubRepository {

    @Id
    private String id;

    private String githubRepoId;

    private String repoName;

    @Indexed(unique = true)
    private String fullName; // owner/repo

    private String owner;

    private String description;

    private String defaultBranch;

    private String visibility; // PUBLIC / PRIVATE / INTERNAL

    private String language;

    private String license;

    @Builder.Default
    private List<String> topics = new ArrayList<>();

    @Builder.Default
    private boolean archived = false;

    @Builder.Default
    private boolean linked = false;

    private String linkedProjectId;

    private String cloneUrl;

    private String htmlUrl;

    private int starsCount;

    private int forksCount;

    private int openIssuesCount;

    private Instant lastSyncedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
