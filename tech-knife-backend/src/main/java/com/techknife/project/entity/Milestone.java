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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project_milestones")
public class Milestone {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String title;

    private String description;

    private LocalDate dueDate;

    @Builder.Default
    private Double completionPercentage = 0.0;

    @Indexed
    @Builder.Default
    private MilestoneStatus status = MilestoneStatus.PLANNED;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    public MilestoneStatus getMilestoneStatus() {
        return this.status;
    }
}

