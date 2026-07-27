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
@Document(collection = "asset_maintenance_schedules")
public class MaintenanceSchedule {

    @Id
    private String id;

    private String assetId;

    private String assetCode;

    private String title;

    private String frequency; // MONTHLY, QUARTERLY, ANNUALLY, ONE_TIME

    private LocalDate scheduledDate;

    private String assignedTechnician;

    @Builder.Default
    private String status = "PENDING"; // PENDING, COMPLETED, OVERDUE

    private String notes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
