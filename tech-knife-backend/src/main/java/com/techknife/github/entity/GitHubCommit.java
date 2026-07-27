package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_commits")
public class GitHubCommit {

    @Id
    private String id;

    private String repositoryId;

    @Indexed
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
