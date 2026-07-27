package com.techknife.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthEndProcessDTO {

    private Integer year;
    private Integer month;
    private Integer totalEmployeesProcessed;
    private Integer totalRecordsFrozen;
    private Boolean isFrozen;
    private Instant processedAt;
    private String processedBy;

    private List<MonthlyAttendanceSummaryDTO> summaries;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyAttendanceSummaryDTO {
        private String id;
        private String employeeId;
        private String employeeName;
        private String departmentId;
        private Integer year;
        private Integer month;
        private Integer totalDays;
        private Double presentDays;
        private Double absentDays;
        private Double leaveDays;
        private Double wfhDays;
        private Integer lateDays;
        private Double overtimeHours;
        private Double payableDays;
        private Boolean isFrozen;
        private Boolean isPayrollReady;
    }
}
