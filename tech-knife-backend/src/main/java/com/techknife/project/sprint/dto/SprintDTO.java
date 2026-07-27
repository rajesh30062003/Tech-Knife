package com.techknife.project.sprint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintDTO {

    private String id;
    private String projectId;
    private String sprintName;
    private String sprintGoal;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private List<String> taskIds;
    private Instant createdAt;
    private Instant updatedAt;
}
