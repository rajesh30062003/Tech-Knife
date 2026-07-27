package com.techknife.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset_assets")
public class Asset {

    @Id
    private String id;

    @Indexed(unique = true)
    private String assetCode;

    private String name;

    private String categoryId;

    private String categoryName;

    @Indexed(unique = true, sparse = true)
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

    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE, ASSIGNED, UNDER_MAINTENANCE, DISPOSED, IN_TRANSIT

    private String currentLocation;

    private String qrCode;

    private String barcode;

    private String remarks;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
