package com.techknife.asset.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asset_software_licenses")
public class SoftwareLicense {

    @Id
    private String id;

    @Indexed(unique = true)
    private String licenseKey;

    private String softwareName;

    private String vendor;

    private LocalDate purchaseDate;

    private LocalDate expiryDate;

    private Integer seatsPurchased;

    @Builder.Default
    private Integer seatsUsed = 0;

    private BigDecimal cost;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, EXPIRED, CANCELLED

    @Builder.Default
    private List<String> assignedEmployeeIds = new ArrayList<>();

    @Builder.Default
    private List<String> assignedEmployeeNames = new ArrayList<>();

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
