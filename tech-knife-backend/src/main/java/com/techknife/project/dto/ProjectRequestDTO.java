package com.techknife.project.dto;

import com.techknife.project.entity.ProjectPriority;
import com.techknife.project.entity.ProjectStatus;
import com.techknife.project.entity.ProjectType;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Project code is required")
    private String projectCode;

    @NotBlank(message = "Project name is required")
    private String projectName;

    private String shortName;

    private String description;

    private String client;

    @Builder.Default
    private ProjectType projectType = ProjectType.FIXED_BID;

    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNED;

    @Builder.Default
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double estimatedHours;

    private Double budget;

    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();

    private String repositoryUrl;

    private String projectManagerId;

    private String logoUrl;
}
