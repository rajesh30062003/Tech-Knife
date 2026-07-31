package com.techknife.project.dto;

import com.techknife.project.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {

    private String id;
    private String projectId;
    private String projectCode;
    private String projectName;
    private String shortName;
    private String description;
    private String objectives;
    private String client;
    private String clientId;
    private String clientOrganization;
    private String department;
    private String category;
    private String businessUnit;
    private ProjectType projectType;
    private ProjectStatus status;
    private ProjectPriority priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate targetEndDate;
    private LocalDate estimatedCompletion;
    private Double estimatedHours;
    private Double estimatedDuration;
    private Double budget;
    private Double estimatedCost;
    private Double progressPercentage;
    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();
    @Builder.Default
    private List<String> programmingLanguages = new ArrayList<>();
    @Builder.Default
    private List<String> frameworks = new ArrayList<>();
    private String databaseTech;
    private String cloudProvider;
    private String repositoryUrl;
    private String repositoryType;
    private String repositoryVisibility;
    private String projectVisibility;
    private String deploymentType;
    private String projectManagerId;
    private String projectManagerName;
    private String projectLeadId;
    private String projectLeadName;
    private String projectSponsor;
    private String customerRepresentative;
    @Builder.Default
    private List<String> assignedEmployees = new ArrayList<>();
    @Builder.Default
    private List<String> assignedInterns = new ArrayList<>();
    private ProjectLinks links;
    private String remarks;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();
    @Builder.Default
    private List<ProjectTeam> teams = new ArrayList<>();
    @Builder.Default
    private List<ProjectDocument> documents = new ArrayList<>();
    private String logoUrl;
    private Double overallProgressPercentage;
    private Integer totalTasks;
    private Integer completedTasks;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
