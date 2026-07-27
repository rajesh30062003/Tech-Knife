package com.techknife.customerportal.dto;

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
public class CustomerTaskDTO {

    private String id;
    private String projectId;
    private String milestoneId;
    private String customerAccountId;
    private String taskName;
    private String description;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private String assigneeName;
    private Instant completedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
