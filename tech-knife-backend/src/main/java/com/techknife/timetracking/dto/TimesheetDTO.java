package com.techknife.timetracking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetDTO {

    private String id;
    private String employeeId;
    private String periodType;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private Double totalHours;
    private Double billableHours;
    private Double nonBillableHours;
    private String status;
    private String approverId;
    private String rejectionReason;
    private List<String> timeEntryIds;
    private List<TimeEntryDTO> timeEntries;
    private Instant submittedAt;
    private Instant approvedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
