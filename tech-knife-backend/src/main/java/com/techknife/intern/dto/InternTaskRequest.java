package com.techknife.intern.dto;

import com.techknife.intern.entity.TaskPriority;
import com.techknife.intern.entity.TaskStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;
    private String assignedBy;
    private LocalDate deadline;
    private TaskPriority priority;
    private TaskStatus status;

    @Min(value = 0, message = "Progress percentage cannot be negative")
    @Max(value = 100, message = "Progress percentage cannot exceed 100")
    private int progressPercentage;

    private String mentorReview;
}
