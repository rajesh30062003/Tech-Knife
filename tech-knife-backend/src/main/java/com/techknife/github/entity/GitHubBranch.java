package com.techknife.github.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_branches")
public class GitHubBranch {

    @Id
    private String id;

    private String repositoryId;

    private String name;

    @Builder.Default
    private boolean isDefault = false;

    @Builder.Default
    private boolean isProtected = false;

    private String creatorUsername;

    private String lastCommitSha;

    private String lastCommitMessage;

    private Instant lastCommitDate;

    @LastModifiedDate
    private Instant updatedAt;
}
