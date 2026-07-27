package com.techknife.project.dto;

import com.techknife.project.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubTaskDTO {

    private String id;

    @NotBlank(message = "Title is required")
    private String title;

    @Builder.Default
    private TaskStatus status = TaskStatus.TODO;

    private String assignedEmployeeId;

    private String assignedEmployeeName;

    @Builder.Default
    private boolean completed = false;
}
