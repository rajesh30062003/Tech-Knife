package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLinks {
    private String githubUrl;
    private String frontendRepoUrl;
    private String backendRepoUrl;
    private String dockerRepoUrl;
    private String cicdPipelineUrl;
    private String deploymentUrl;
    private String stagingUrl;
    private String productionUrl;
    private String testingUrl;
    private String kubernetesDashboardUrl;
    private String serverUrl;
    private String domainName;
    private String documentationUrl;
    private String apiDocUrl;
    private String swaggerUrl;
    private String figmaUrl;
    private String googleDriveUrl;
    private String driveUrl;
    private String jiraUrl;
    private String notionUrl;
    private String confluenceUrl;
    private String monitoringUrl;
    private String envRef;
    private String serverDetails;
}
