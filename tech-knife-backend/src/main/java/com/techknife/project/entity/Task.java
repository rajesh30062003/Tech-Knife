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
@Document(collection = "project_tasks")
public class Task {

    @Id
    private String id;

    @Indexed(unique = true)
    private String taskNumber;

    @Indexed
    private String projectId;

    private String title;

    private String description;

    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIUM;

    @Indexed
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    @Builder.Default
    private Integer storyPoints = 1;

    @Builder.Default
    private Double estimatedHours = 0.0;

    @Indexed
    private String assignedEmployeeId;

    private String assignedEmployeeName;

    private String reviewerId;

    private String reviewerName;

    @Indexed
    private String milestoneId;

    @Indexed
    private String parentTaskId;

    private LocalDate dueDate;

    @Builder.Default
    private Double completionPercentage = 0.0;

    @Builder.Default
    private List<String> labels = new ArrayList<>();

    @Builder.Default
    private List<ChecklistItem> checklist = new ArrayList<>();

    @Builder.Default
    private List<SubTask> subtasks = new ArrayList<>();

    private String sprint;

    private String epic;

    @Builder.Default
    private Double loggedHours = 0.0;

    @Builder.Default
    private List<String> dependencies = new ArrayList<>();

    private Object createdByInfo;

    private Object completedByInfo;

    private LocalDate completedDate;

    @Builder.Default
    private Integer votesCount = 0;

    @Builder.Default
    private Boolean isPinned = false;

    @Builder.Default
    private Boolean isWatching = false;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    public TaskStatus getTaskStatus() {
        return this.status;
    }

    public void setTaskStatus(TaskStatus status) {
        this.status = status;
    }
}


