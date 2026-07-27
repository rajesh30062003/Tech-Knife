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
public class AssetWarrantyDTO {

    private String id;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String assetCode;

    private String providerName;

    private String contactPhone;

    private String contactEmail;

    private LocalDate startDate;

    private LocalDate endDate;

    private String coverageDetails;

    private String terms;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;
}
