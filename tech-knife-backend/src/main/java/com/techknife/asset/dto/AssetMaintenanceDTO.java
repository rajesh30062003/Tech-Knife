package com.techknife.asset.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetMaintenanceDTO {

    private String id;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String assetCode;

    private String assetName;

    private String maintenanceType;

    private String serviceProvider;

    private String contactPerson;

    private LocalDate maintenanceDate;

    private LocalDate completionDate;

    private BigDecimal cost;

    private String status;

    private String description;

    private String resolution;

    private String amcContractNumber;

    private Instant createdAt;

    private Instant updatedAt;
}
