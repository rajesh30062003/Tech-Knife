package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

/**
 * MongoDB Document representing employee assignment to projects.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project_assignments")
public class ProjectAssignment {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    @Indexed
    private String projectId;

    private String projectName;

    private String role;

    @Builder.Default
    private Double allocationPercentage = 100.0;

    private LocalDate assignedDate;

    private LocalDate releasedDate;

    @Builder.Default
    private String status = "ACTIVE";

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
