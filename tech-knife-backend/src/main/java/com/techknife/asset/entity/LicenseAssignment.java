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
@Document(collection = "asset_license_assignments")
public class LicenseAssignment {

    @Id
    private String id;

    private String licenseId;

    private String softwareName;

    private String employeeId;

    private String employeeName;

    private LocalDate assignedDate;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, REVOKED

    @CreatedDate
    private Instant createdAt;
}
