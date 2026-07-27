package com.techknife.project.dto;

import com.techknife.project.entity.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusUpdateDTO {

    @NotNull(message = "New project status is required")
    private ProjectStatus status;

    private String reason;
}
