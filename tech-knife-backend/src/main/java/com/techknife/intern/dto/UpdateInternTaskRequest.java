package com.techknife.intern.dto;

import com.techknife.intern.entity.TaskPriority;
import com.techknife.intern.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInternTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private TaskStatus status;
    private Integer progressPercentage;
    private LocalDate deadline;
    private LocalDate completionDate;
    private String reviewRemarks;
}
