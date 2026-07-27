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
public class ExpenseReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryExpenseBreakdown {
        private String categoryId;
        private String categoryName;
        private BigDecimal totalAmount;
        private Long count;
    }

    private String financialYearId;

    private BigDecimal totalExpenses;

    private List<CategoryExpenseBreakdown> categoryBreakdown;

    private List<ExpenseDTO> expenseList;
}
