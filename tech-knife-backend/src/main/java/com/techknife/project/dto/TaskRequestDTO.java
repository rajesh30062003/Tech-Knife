package com.techknife.project.dto;

import com.techknife.project.entity.ChecklistItem;
import com.techknife.project.entity.SubTask;
import com.techknife.project.entity.TaskPriority;
import com.techknife.project.entity.TaskStatus;
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
public class TaskRequestDTO {

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Builder.Default
    private Integer storyPoints = 1;

    private Double estimatedHours;

    private String assignedEmployeeId;

    private String reviewerId;

    private String milestoneId;

    private String parentTaskId;

    private LocalDate dueDate;

    private Double completionPercentage;

    @Builder.Default
    private List<String> labels = new ArrayList<>();

    @Builder.Default
    private List<ChecklistItem> checklist = new ArrayList<>();

    @Builder.Default
    private List<SubTask> subtasks = new ArrayList<>();
}
