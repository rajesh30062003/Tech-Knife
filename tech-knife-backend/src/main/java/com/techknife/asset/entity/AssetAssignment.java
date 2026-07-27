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
@Document(collection = "asset_assignments")
public class AssetAssignment {

    @Id
    private String id;

    private String assetId;

    private String assetCode;

    private String assetName;

    private String employeeId;

    private String employeeName;

    private String departmentId;

    private String departmentName;

    private LocalDate assignmentDate;

    private LocalDate expectedReturnDate;

    private LocalDate actualReturnDate;

    private String assignedBy;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, RETURNED, TRANSFERRED

    private String returnCondition;

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
