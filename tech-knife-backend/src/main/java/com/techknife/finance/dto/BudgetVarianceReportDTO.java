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
public class BudgetVarianceReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetVarianceRow {
        private String budgetId;
        private String budgetName;
        private String budgetScope;
        private String departmentOrProject;
        private BigDecimal budgetedAmount;
        private BigDecimal actualAmount;
        private BigDecimal varianceAmount;
        private Double variancePercentage;
    }

    private String financialYearId;

    private List<BudgetVarianceRow> budgetRows;

    private BigDecimal totalBudgeted;

    private BigDecimal totalActual;

    private BigDecimal totalVariance;
}
