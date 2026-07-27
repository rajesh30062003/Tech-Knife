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
public class VendorReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VendorSummary {
        private String vendorId;
        private String vendorCode;
        private String vendorName;
        private BigDecimal totalPurchases;
        private BigDecimal outstandingBalance;
        private Long poCount;
    }

    private List<VendorSummary> vendors;

    private BigDecimal totalPurchasesAllVendors;

    private BigDecimal totalOutstandingAllVendors;
}
