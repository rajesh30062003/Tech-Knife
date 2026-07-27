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
    private String projectCode;
    private String projectName;
    private String shortName;
    private String description;
    private String client;
    private ProjectType projectType;
    private ProjectStatus status;
    private ProjectPriority priority;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedHours;
    private Double budget;
    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();
    private String repositoryUrl;
    private String projectManagerId;
    private String projectManagerName;
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
