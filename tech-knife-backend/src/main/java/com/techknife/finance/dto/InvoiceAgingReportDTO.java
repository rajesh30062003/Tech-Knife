package com.techknife.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAgingReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerAgingSummary {
        private String customerId;
        private String customerName;
        private BigDecimal currentAmount;   // 0-30 days
        private BigDecimal days30To60;      // 31-60 days
        private BigDecimal days60To90;      // 61-90 days
        private BigDecimal over90Days;      // 90+ days
        private BigDecimal totalOutstanding;
    }

    private List<CustomerAgingSummary> agingSummaries;

    private BigDecimal totalCurrent;

    private BigDecimal total30To60;

    private BigDecimal total60To90;

    private BigDecimal totalOver90;

    private BigDecimal grandTotalOutstanding;
}
