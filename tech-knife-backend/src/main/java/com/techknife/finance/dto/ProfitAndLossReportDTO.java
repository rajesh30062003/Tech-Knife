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
public class ProfitAndLossReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSummary {
        private String accountId;
        private String accountCode;
        private String accountName;
        private BigDecimal amount;
    }

    private String financialYearId;

    private List<AccountSummary> revenueAccounts;

    private BigDecimal totalRevenue;

    private List<AccountSummary> expenseAccounts;

    private BigDecimal totalExpenses;

    private BigDecimal netProfitOrLoss;
}
