package com.techknife.intern.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternAttendanceSummaryResponse {
    private String id;
    private String internId;
    private String monthYear;
    private Integer totalWorkingDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    private Double attendancePercentage;
    private Instant updatedAt;
}
