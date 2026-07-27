package com.techknife.intern.dto;

import com.techknife.intern.entity.TaskPriority;
import com.techknife.intern.entity.TaskStatus;
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
public class InternTaskResponse {
    private String id;
    private String internId;
    private String mentorId;
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private Integer progressPercentage;
    private LocalDate deadline;
    private LocalDate assignedDate;
    private LocalDate completionDate;
    private String reviewRemarks;
    private Instant createdAt;
    private Instant updatedAt;
}
