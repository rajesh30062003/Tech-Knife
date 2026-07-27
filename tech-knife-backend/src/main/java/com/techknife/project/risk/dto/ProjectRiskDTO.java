package com.techknife.project.risk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRiskDTO {

    private String id;
    private String projectId;
    private String title;
    private String description;
    private String impact;
    private String probability;
    private String ownerId;
    private String mitigationPlan;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
