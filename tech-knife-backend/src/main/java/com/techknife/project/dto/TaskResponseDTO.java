package com.techknife.project.dto;

import com.techknife.project.entity.ChecklistItem;
import com.techknife.project.entity.SubTask;
import com.techknife.project.entity.TaskPriority;
import com.techknife.project.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private String id;
    private String taskNumber;
    private String projectId;
    private String projectName;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private Integer storyPoints;
    private Double estimatedHours;
    private String assignedEmployeeId;
    private String assignedEmployeeName;
    private String reviewerId;
    private String reviewerName;
    private String milestoneId;
    private String milestoneTitle;
    private String parentTaskId;
    private LocalDate dueDate;
    private Double completionPercentage;
    @Builder.Default
    private List<String> labels = new ArrayList<>();
    @Builder.Default
    private List<ChecklistItem> checklist = new ArrayList<>();
    @Builder.Default
    private List<SubTask> subtasks = new ArrayList<>();
    private Integer totalSubtasks;
    private Integer completedSubtasks;
    private String sprint;
    private String epic;
    private Double loggedHours;
    @Builder.Default
    private List<String> dependencies = new ArrayList<>();
    private Object createdByInfo;
    private Object completedByInfo;
    private LocalDate completedDate;
    private Integer votesCount;
    private Boolean isPinned;
    private Boolean isWatching;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
