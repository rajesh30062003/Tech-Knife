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
public class FinanceDashboardDTO {

    private BigDecimal totalRevenue;

    private BigDecimal totalExpenses;

    private BigDecimal netProfit;

    private BigDecimal outstandingInvoicesAmount;

    private Long outstandingInvoicesCount;

    private BigDecimal cashPosition;

    private Double budgetUtilizationPercentage;

    private List<JournalEntryDTO> recentJournalEntries;

    private List<InvoiceDTO> recentInvoices;

    private List<ExpenseDTO> recentExpenses;
}
