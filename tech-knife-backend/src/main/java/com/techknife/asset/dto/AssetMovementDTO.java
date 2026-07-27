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
public class AssetMovementDTO {

    private String id;

    @NotBlank(message = "Asset ID is required")
    private String assetId;

    private String assetCode;

    private String assetName;

    private String fromLocation;

    private String toLocation;

    private String fromBranch;

    private String toBranch;

    private LocalDate movementDate;

    private String movedBy;

    private String reason;

    private String status;

    private Instant createdAt;
}
