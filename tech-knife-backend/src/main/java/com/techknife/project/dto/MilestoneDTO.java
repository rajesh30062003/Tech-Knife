package com.techknife.project.dto;

import com.techknife.project.entity.MilestoneStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneDTO {

    private String id;

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @Builder.Default
    private Double completionPercentage = 0.0;

    @Builder.Default
    private MilestoneStatus status = MilestoneStatus.PLANNED;

    private Instant createdAt;
    private Instant updatedAt;
}
