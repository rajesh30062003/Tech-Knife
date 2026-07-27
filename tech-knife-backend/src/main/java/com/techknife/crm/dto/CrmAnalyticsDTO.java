package com.techknife.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrmAnalyticsDTO {
    private Double leadConversionRate; // % converted
    private Map<String, Double> salesPerformanceByAccountManager; // total value won per account manager
    private Map<String, Long> topLeadSourcesCount;
    private Map<String, Double> topLeadSourcesConversionRate;
    private Double averageDealSize;
    private Double averageSalesCycleDays;
    private Map<String, Long> customerAcquisitionTrendMonthly; // month string -> count
}
