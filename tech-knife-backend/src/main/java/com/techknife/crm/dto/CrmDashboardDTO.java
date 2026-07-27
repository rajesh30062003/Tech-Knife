package com.techknife.crm.dto;

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
public class CrmDashboardDTO {
    private Long totalLeads;
    private Map<String, Long> leadsByStatus;
    private Map<String, Long> leadsBySource;

    private Map<String, Long> salesFunnelCountByStage;
    private Map<String, Double> salesFunnelValueByStage;

    private Double totalPipelineValue;
    private Long wonDealsCount;
    private Double wonDealsValue;
    private Long lostDealsCount;
    private Double lostDealsValue;

    private List<FollowUpDTO> upcomingFollowUps;
    private List<CustomerDTO> recentCustomers;
    private Double revenueForecast;
}
