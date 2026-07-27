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
public class BalanceSheetReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummary {
        private String accountId;
        private String accountCode;
        private String accountName;
        private BigDecimal balance;
    }

    private String financialYearId;

    private List<AccountSummary> assetAccounts;

    private BigDecimal totalAssets;

    private List<AccountSummary> liabilityAccounts;

    private BigDecimal totalLiabilities;

    private List<AccountSummary> equityAccounts;

    private BigDecimal totalEquity;

    private BigDecimal totalLiabilitiesAndEquity;

    private Boolean isBalanced;
}
