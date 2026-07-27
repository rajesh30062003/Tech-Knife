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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "github_deployments")
public class GitHubDeployment {

    @Id
    private String id;

    private String repositoryId;

    private String environment; // production, staging, dev

    private String buildNumber;

    private String commitSha;

    private String status; // PENDING, IN_PROGRESS, SUCCESS, FAILURE, ROLLED_BACK

    private Instant deploymentTime;

    private String deployedBy;

    private String rollbackFromDeploymentId;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
