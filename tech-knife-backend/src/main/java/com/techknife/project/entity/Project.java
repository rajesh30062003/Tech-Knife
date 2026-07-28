package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectCode;

    private String projectName;

    private String shortName;

    private String description;

    private String client;

    @Builder.Default
    private ProjectType projectType = ProjectType.FIXED_BID;

    @Indexed
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNED;

    @Builder.Default
    private ProjectPriority priority = ProjectPriority.MEDIUM;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private Double estimatedHours = 0.0;

    @Builder.Default
    private Double budget = 0.0;

    @Builder.Default
    private List<String> technologyStack = new ArrayList<>();

    private String repositoryUrl;

    @Indexed
    private String projectManagerId;

    private String projectManagerName;

    @Builder.Default
    private List<ProjectMember> members = new ArrayList<>();

    @Builder.Default
    private List<ProjectTeam> teams = new ArrayList<>();

    @Builder.Default
    private List<ProjectDocument> documents = new ArrayList<>();

    private String logoUrl;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    public Double getSpentAmount() {
        return 0.0;
    }
}

