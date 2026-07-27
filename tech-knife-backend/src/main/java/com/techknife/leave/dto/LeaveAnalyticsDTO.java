package com.techknife.leave.dto;

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
public class LeaveAnalyticsDTO {

    private double totalLeaveConsumption;
    private double averageLeaveDaysPerEmployee;
    private Map<String, Double> departmentLeaveTrend;
    private Map<String, Double> leaveTypeDistribution;
    private Map<String, Double> wfhTrend;
    private Map<String, Double> holidayUtilization;
    private List<MonthlyTrend> monthlyConsumptionTrend;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String month;
        private double leaveDays;
        private double wfhDays;
    }
}
