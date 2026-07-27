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
public class AssetDTO {

    private String id;

    @NotBlank(message = "Asset code is required")
    private String assetCode;

    @NotBlank(message = "Asset name is required")
    private String name;

    private String categoryId;

    private String categoryName;

    private String serialNumber;

    private String brand;

    private String model;

    private String configuration;

    private LocalDate purchaseDate;

    private BigDecimal purchaseCost;

    private LocalDate warrantyStartDate;

    private LocalDate warrantyEndDate;

    private String assignedEmployeeId;

    private String assignedEmployeeName;

    private String assignedDepartmentId;

    private String assignedDepartmentName;

    private String assignedBranchId;

    private String status;

    private String currentLocation;

    private String qrCode;

    private String barcode;

    private String remarks;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}
