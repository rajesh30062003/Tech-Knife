package com.techknife.project.sprint.dto;

import com.techknife.project.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintBoardDTO {

    private SprintDTO sprint;
    private Map<String, List<Task>> tasksByStatus; // e.g. TODO, IN_PROGRESS, IN_REVIEW, COMPLETED, BLOCKED
    private Integer totalTasks;
    private Integer completedTasks;
    private Double completionPercentage;
}
