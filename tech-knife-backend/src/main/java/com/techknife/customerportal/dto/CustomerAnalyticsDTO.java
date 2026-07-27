package com.techknife.customerportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAnalyticsDTO {

    private double projectCompletionRate; // Percentage e.g. 85.5
    private double avgTicketResolutionTimeHours; // e.g. 12.4
    private double customerSatisfactionScore; // e.g. 9.2 out of 10
    private long openIssuesCount;
    private Map<String, Long> invoiceStatusBreakdown; // UNPAID: 2, PAID: 10, OVERDUE: 1
    private Map<String, Long> projectStatusBreakdown; // IN_PROGRESS: 3, COMPLETED: 8
    private Map<String, Long> ticketPriorityBreakdown; // HIGH: 1, MEDIUM: 2
}
