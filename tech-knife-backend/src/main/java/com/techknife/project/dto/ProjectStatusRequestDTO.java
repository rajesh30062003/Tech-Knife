package com.techknife.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusRequestDTO {

    @NotBlank(message = "Requested status is required")
    private String requestedStatus;

    private String reason;

    private String requestedBy;

    private String requestedByRole;
}
