package com.techknife.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset_warranties")
public class AssetWarranty {

    @Id
    private String id;

    private String assetId;

    private String assetCode;

    private String providerName;

    private String contactPhone;

    private String contactEmail;

    private LocalDate startDate;

    private LocalDate endDate;

    private String coverageDetails;

    private String terms;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, EXPIRED, CLAIMED

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
