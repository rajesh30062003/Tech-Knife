package com.techknife.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryDto {
    private String userId;
    private String userName;
    private String department;
    private int periodYear;
    private Integer periodMonth;
    
    private int totalDays;
    private int presentDays;
    private int absentDays;
    private int lateDays;
    private int halfDays;
    private int wfhDays;
    private int holidayDays;
    private int weekendDays;
    private int leaveDays;

    private double totalWorkingHours;
    private double totalOvertimeHours;
    private double averageDailyHours;
    private double attendancePercentage;

    private Map<String, Integer> statusBreakdown;
}
