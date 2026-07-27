package com.techknife.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveReportDTO {

    private String id;
    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalDays;
    private String reason;
    private String status;
    private String appliedAt;
}
