package com.techknife.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTask {
    private String id;
    private String title;
    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;
    private String assignedEmployeeId;
    private String assignedEmployeeName;
    @Builder.Default
    private boolean completed = false;
    private Instant createdAt;
    private Instant updatedAt;
}
