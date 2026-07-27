package com.techknife.asset.dto;

import jakarta.validation.constraints.NotBlank;
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
public class MaintenanceScheduleDTO {

    private String id;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String assetCode;

    private String title;

    private String frequency;

    private LocalDate scheduledDate;

    private String assignedTechnician;

    private String status;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;
}
