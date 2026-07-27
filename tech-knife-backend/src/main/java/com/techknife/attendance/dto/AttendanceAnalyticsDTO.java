package com.techknife.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceAnalyticsDTO {

    private double monthlyAttendancePercentage;
    private double averageWorkingHours;
    private List<TrendData> lateArrivalTrend;
    private List<TrendData> earlyExitTrend;
    private List<TrendData> overtimeTrend;
    private Map<String, Double> departmentAttendance;
    private Map<String, Double> branchAttendance;
    private Map<String, Double> shiftUtilization;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String label; // e.g. "Week 1", "Jan 01"
        private double value;
    }
}
