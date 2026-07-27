package com.techknife.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset_maintenances")
public class AssetMaintenance {

    @Id
    private String id;

    private String assetId;

    private String assetCode;

    private String assetName;

    private String maintenanceType; // PREVENTIVE, CORRECTIVE, AMC, REPAIR

    private String serviceProvider;

    private String contactPerson;

    private LocalDate maintenanceDate;

    private LocalDate completionDate;

    private BigDecimal cost;

    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED

    private String description;

    private String resolution;

    private String amcContractNumber;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
