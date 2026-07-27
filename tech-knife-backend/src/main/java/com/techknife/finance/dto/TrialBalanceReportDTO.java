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
public class TrialBalanceReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrialBalanceRow {
        private String accountId;
        private String accountCode;
        private String accountName;
        private String accountType;
        private BigDecimal debitAmount;
        private BigDecimal creditAmount;
    }

    private String financialYearId;

    private List<TrialBalanceRow> rows;

    private BigDecimal totalDebit;

    private BigDecimal totalCredit;

    private Boolean isBalanced;
}
