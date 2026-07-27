package com.techknife.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiReportDTO {
    private String metricKey;
    private String title;
    private Object currentValue;
    private Object previousValue;
    private Double percentageChange;
    private String unit;
    private Map<String, Object> breakdown;
    private Instant calculatedAt;

    // Standard KPI metrics
    private Long employeeCount;
    private Double attendancePercentage;
    private Double leaveUtilizationRate;
    private BigDecimal payrollTotalCost;
    private Map<String, Long> recruitmentFunnel;
    private Double leadConversionRate;
    private Double projectCompletionPercentage;
    private BigDecimal revenue;
    private BigDecimal expenses;
    private BigDecimal profit;
    private BigDecimal inventoryValue;
    private Double assetUtilizationPercentage;
}
