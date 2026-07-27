package com.techknife.finance.service;

import com.techknife.finance.dto.*;

import java.time.LocalDate;

public interface FinanceReportService {

    GeneralLedgerReportDTO getGeneralLedgerReport(String accountId, LocalDate startDate, LocalDate endDate);

    TrialBalanceReportDTO getTrialBalanceReport(String financialYearId);

    ProfitAndLossReportDTO getProfitAndLossReport(String financialYearId, LocalDate startDate, LocalDate endDate);

    BalanceSheetReportDTO getBalanceSheetReport(String financialYearId);

    CashFlowReportDTO getCashFlowReport(String financialYearId);

    ExpenseReportDTO getExpenseReport(String financialYearId, LocalDate startDate, LocalDate endDate);

    RevenueReportDTO getRevenueReport(String financialYearId, LocalDate startDate, LocalDate endDate);

    VendorReportDTO getVendorReport();

    InvoiceAgingReportDTO getInvoiceAgingReport();

    BudgetVarianceReportDTO getBudgetVarianceReport(String financialYearId);
}
