package com.techknife.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDashboardDTO {

    private LocalDate date;
    private long totalEmployees;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private long earlyExitCount;
    private long onLeaveCount;
    private long wfhCount;
    private long overtimeCount;
    private double totalOvertimeHoursToday;
    private long pendingCorrectionsCount;
    private double monthlyAttendancePercentage;

    private List<EmployeeAttendanceSummary> lateEmployees;
    private List<EmployeeAttendanceSummary> earlyExitEmployees;
    private List<EmployeeAttendanceSummary> onLeaveEmployees;
    private List<EmployeeAttendanceSummary> wfhEmployees;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeAttendanceSummary {
        private String employeeId;
        private String employeeName;
        private String departmentId;
        private String time; // checkIn or checkOut or status
        private String remarks;
    }
}
