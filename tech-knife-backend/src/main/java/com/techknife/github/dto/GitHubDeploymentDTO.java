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
public class GitHubDeploymentDTO {
    private String id;
    private String repositoryId;
    private String environment;
    private String buildNumber;
    private String commitSha;
    private String status;
    private Instant deploymentTime;
    private String deployedBy;
    private String rollbackFromDeploymentId;
    private Instant createdAt;
    private Instant updatedAt;
}
