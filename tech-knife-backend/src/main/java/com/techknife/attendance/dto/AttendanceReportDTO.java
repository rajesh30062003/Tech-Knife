package com.techknife.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportDTO {

    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String branchId;
    private LocalDate date;
    private String checkIn;
    private String checkOut;
    private String status;
    private Double workHours;
    private Double overtimeHours;
    private Boolean isLate;
    private Boolean isEarlyExit;
    private Integer lateMinutes;
    private Integer earlyExitMinutes;
    private String remarks;
}
