package com.techknife.timetracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartTimerRequest {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String projectId;

    private String taskId;

    private String description;

    @Builder.Default
    private boolean billable = true;
}
