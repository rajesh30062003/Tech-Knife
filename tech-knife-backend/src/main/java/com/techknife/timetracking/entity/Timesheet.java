package com.techknife.timetracking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "timesheets")
public class Timesheet {

    @Id
    private String id;

    @Indexed
    private String employeeId;

    private String periodType; // DAILY, WEEKLY, MONTHLY

    private LocalDate periodStartDate;

    private LocalDate periodEndDate;

    private Double totalHours;

    private Double billableHours;

    private Double nonBillableHours;

    @Builder.Default
    private String status = "DRAFT"; // DRAFT, SUBMITTED, APPROVED, REJECTED

    private String approverId;

    private String rejectionReason;

    @Builder.Default
    private List<String> timeEntryIds = new ArrayList<>();

    private Instant submittedAt;

    private Instant approvedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
