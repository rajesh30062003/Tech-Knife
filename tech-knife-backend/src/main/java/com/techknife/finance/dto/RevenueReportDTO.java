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
public class RevenueReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerRevenueBreakdown {
        private String customerId;
        private String customerName;
        private BigDecimal totalRevenue;
        private Long invoiceCount;
    }

    private String financialYearId;

    private BigDecimal totalRevenue;

    private List<CustomerRevenueBreakdown> customerBreakdown;

    private List<InvoiceDTO> invoiceList;
}
