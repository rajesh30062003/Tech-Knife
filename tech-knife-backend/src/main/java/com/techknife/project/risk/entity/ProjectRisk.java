package com.techknife.project.risk.entity;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "project_risks")
public class ProjectRisk {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String title;

    private String description;

    private String impact; // LOW, MEDIUM, HIGH, CRITICAL

    private String probability; // LOW, MEDIUM, HIGH

    private String ownerId;

    private String mitigationPlan;

    @Builder.Default
    private String status = "IDENTIFIED"; // IDENTIFIED, MITIGATED, OPEN, CLOSED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
