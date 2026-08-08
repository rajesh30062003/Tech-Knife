package com.techknife.project.dto;

import com.techknife.project.entity.ProjectLinks;
import com.techknife.project.entity.ProjectPendingStatusRequest;
import com.techknife.project.entity.ProjectPriority;
import com.techknife.project.entity.ProjectStatus;
import com.techknife.project.entity.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {

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

    @Builder.Default
    private ProjectType projectType = ProjectType.FIXED_BID;

    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNED;

    @Builder.Default
    private ProjectPriority priority = ProjectPriority.MEDIUM;

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

    private String projectLeadId;

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

    private String logoUrl;

    private ProjectPendingStatusRequest pendingStatusRequest;
}
