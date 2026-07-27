package com.techknife.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset_movements")
public class AssetMovement {

    @Id
    private String id;

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

    @Builder.Default
    private String status = "COMPLETED"; // IN_TRANSIT, COMPLETED, CANCELLED

    @CreatedDate
    private Instant createdAt;
}
