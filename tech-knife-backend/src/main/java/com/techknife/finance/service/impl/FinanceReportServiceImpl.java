package com.techknife.finance.service.impl;

import com.techknife.finance.dto.*;
import com.techknife.finance.entity.*;
import com.techknife.finance.repository.*;
import com.techknife.finance.service.FinanceReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceReportServiceImpl implements FinanceReportService {

    private final ChartOfAccountRepository chartOfAccountRepository;
    private final GeneralLedgerRepository generalLedgerRepository;
    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final VendorRepository vendorRepository;
    private final BudgetRepository budgetRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;

    @Override
    public GeneralLedgerReportDTO getGeneralLedgerReport(String accountId, LocalDate startDate, LocalDate endDate) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        List<GeneralLedger> entries;
        if (startDate != null && endDate != null) {
            entries = generalLedgerRepository.findByAccountIdAndTransactionDateBetween(accountId, startDate, endDate);
        } else {
            entries = generalLedgerRepository.findByAccountId(accountId);
        }

        BigDecimal totalDebit = entries.stream()
                .map(e -> e.getDebitAmount() != null ? e.getDebitAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = entries.stream()
                .map(e -> e.getCreditAmount() != null ? e.getCreditAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal opening = account.getOpeningBalance() != null ? account.getOpeningBalance() : BigDecimal.ZERO;
        BigDecimal closing;
        String type = account.getAccountType() != null ? account.getAccountType().toUpperCase() : "ASSETS";
        if ("ASSETS".equals(type) || "EXPENSES".equals(type)) {
            closing = opening.add(totalDebit).subtract(totalCredit);
        } else {
            closing = opening.add(totalCredit).subtract(totalDebit);
        }

        List<GeneralLedgerDTO> dtoList = entries.stream().map(gl -> GeneralLedgerDTO.builder()
                .id(gl.getId())
                .accountId(gl.getAccountId())
                .accountCode(gl.getAccountCode())
                .accountName(gl.getAccountName())
                .journalEntryId(gl.getJournalEntryId())
                .journalNumber(gl.getJournalNumber())
                .referenceNumber(gl.getReferenceNumber())
                .transactionDate(gl.getTransactionDate())
                .financialYearId(gl.getFinancialYearId())
                .costCenterId(gl.getCostCenterId())
                .debitAmount(gl.getDebitAmount())
                .creditAmount(gl.getCreditAmount())
                .openingBalance(gl.getOpeningBalance())
                .closingBalance(gl.getClosingBalance())
                .narration(gl.getNarration())
                .createdAt(gl.getCreatedAt())
                .build()).collect(Collectors.toList());

        return GeneralLedgerReportDTO.builder()
                .accountId(account.getId())
                .accountCode(account.getAccountCode())
                .accountName(account.getAccountName())
                .openingBalance(opening)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .closingBalance(closing)
                .entries(dtoList)
                .build();
    }

    @Override
    public TrialBalanceReportDTO getTrialBalanceReport(String financialYearId) {
        List<ChartOfAccount> accounts = chartOfAccountRepository.findAll();
        List<TrialBalanceReportDTO.TrialBalanceRow> rows = new ArrayList<>();

        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (ChartOfAccount acc : accounts) {
            BigDecimal bal = acc.getCurrentBalance() != null ? acc.getCurrentBalance() : BigDecimal.ZERO;
            String type = acc.getAccountType() != null ? acc.getAccountType().toUpperCase() : "ASSETS";

            BigDecimal debit = BigDecimal.ZERO;
            BigDecimal credit = BigDecimal.ZERO;

            if ("ASSETS".equals(type) || "EXPENSES".equals(type)) {
                if (bal.compareTo(BigDecimal.ZERO) >= 0) {
                    debit = bal;
                } else {
                    credit = bal.abs();
                }
            } else {
                if (bal.compareTo(BigDecimal.ZERO) >= 0) {
                    credit = bal;
                } else {
                    debit = bal.abs();
                }
            }

            rows.add(TrialBalanceReportDTO.TrialBalanceRow.builder()
                    .accountId(acc.getId())
                    .accountCode(acc.getAccountCode())
                    .accountName(acc.getAccountName())
                    .accountType(acc.getAccountType())
                    .debitAmount(debit)
                    .creditAmount(credit)
                    .build());

            totalDebit = totalDebit.add(debit);
            totalCredit = totalCredit.add(credit);
        }

        return TrialBalanceReportDTO.builder()
                .financialYearId(financialYearId)
                .rows(rows)
                .totalDebit(totalDebit)
                .totalCredit(totalCredit)
                .isBalanced(totalDebit.compareTo(totalCredit) == 0)
                .build();
    }

    @Override
    public ProfitAndLossReportDTO getProfitAndLossReport(String financialYearId, LocalDate startDate, LocalDate endDate) {
        List<ChartOfAccount> revenueAccounts = chartOfAccountRepository.findByAccountType("REVENUE");
        List<ChartOfAccount> expenseAccounts = chartOfAccountRepository.findByAccountType("EXPENSES");

        List<ProfitAndLossReportDTO.AccountSummary> revSummaries = revenueAccounts.stream()
                .map(a -> ProfitAndLossReportDTO.AccountSummary.builder()
                        .accountId(a.getId())
                        .accountCode(a.getAccountCode())
                        .accountName(a.getAccountName())
                        .amount(a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalRev = revSummaries.stream().map(ProfitAndLossReportDTO.AccountSummary::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ProfitAndLossReportDTO.AccountSummary> expSummaries = expenseAccounts.stream()
                .map(a -> ProfitAndLossReportDTO.AccountSummary.builder()
                        .accountId(a.getId())
                        .accountCode(a.getAccountCode())
                        .accountName(a.getAccountName())
                        .amount(a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalExp = expSummaries.stream().map(ProfitAndLossReportDTO.AccountSummary::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return ProfitAndLossReportDTO.builder()
                .financialYearId(financialYearId)
                .revenueAccounts(revSummaries)
                .totalRevenue(totalRev)
                .expenseAccounts(expSummaries)
                .totalExpenses(totalExp)
                .netProfitOrLoss(totalRev.subtract(totalExp))
                .build();
    }

    @Override
    public BalanceSheetReportDTO getBalanceSheetReport(String financialYearId) {
        List<ChartOfAccount> assetAccounts = chartOfAccountRepository.findByAccountType("ASSETS");
        List<ChartOfAccount> liabilityAccounts = chartOfAccountRepository.findByAccountType("LIABILITIES");
        List<ChartOfAccount> equityAccounts = chartOfAccountRepository.findByAccountType("EQUITY");

        List<BalanceSheetReportDTO.AccountSummary> assetSummaries = assetAccounts.stream()
                .map(a -> BalanceSheetReportDTO.AccountSummary.builder()
                        .accountId(a.getId())
                        .accountCode(a.getAccountCode())
                        .accountName(a.getAccountName())
                        .balance(a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                        .build()).collect(Collectors.toList());
        BigDecimal totalAssets = assetSummaries.stream().map(BalanceSheetReportDTO.AccountSummary::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BalanceSheetReportDTO.AccountSummary> liabilitySummaries = liabilityAccounts.stream()
                .map(a -> BalanceSheetReportDTO.AccountSummary.builder()
                        .accountId(a.getId())
                        .accountCode(a.getAccountCode())
                        .accountName(a.getAccountName())
                        .balance(a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                        .build()).collect(Collectors.toList());
        BigDecimal totalLiabilities = liabilitySummaries.stream().map(BalanceSheetReportDTO.AccountSummary::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BalanceSheetReportDTO.AccountSummary> equitySummaries = equityAccounts.stream()
                .map(a -> BalanceSheetReportDTO.AccountSummary.builder()
                        .accountId(a.getId())
                        .accountCode(a.getAccountCode())
                        .accountName(a.getAccountName())
                        .balance(a.getCurrentBalance() != null ? a.getCurrentBalance() : BigDecimal.ZERO)
                        .build()).collect(Collectors.toList());
        BigDecimal totalEquity = equitySummaries.stream().map(BalanceSheetReportDTO.AccountSummary::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLiabEq = totalLiabilities.add(totalEquity);

        return BalanceSheetReportDTO.builder()
                .financialYearId(financialYearId)
                .assetAccounts(assetSummaries)
                .totalAssets(totalAssets)
                .liabilityAccounts(liabilitySummaries)
                .totalLiabilities(totalLiabilities)
                .equityAccounts(equitySummaries)
                .totalEquity(totalEquity)
                .totalLiabilitiesAndEquity(totalLiabEq)
                .isBalanced(totalAssets.compareTo(totalLiabEq) == 0)
                .build();
    }

    @Override
    public CashFlowReportDTO getCashFlowReport(String financialYearId) {
        BigDecimal cashInflows = receiptRepository.findAll().stream()
                .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal cashOutflows = paymentRepository.findAll().stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netFlow = cashInflows.subtract(cashOutflows);

        return CashFlowReportDTO.builder()
                .financialYearId(financialYearId)
                .operatingCashInflow(cashInflows)
                .operatingCashOutflow(cashOutflows)
                .netOperatingCashFlow(netFlow)
                .netCashFlow(netFlow)
                .openingCashBalance(BigDecimal.ZERO)
                .closingCashBalance(netFlow)
                .build();
    }

    @Override
    public ExpenseReportDTO getExpenseReport(String financialYearId, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses;
        if (startDate != null && endDate != null) {
            expenses = expenseRepository.findByExpenseDateBetween(startDate, endDate);
        } else {
            expenses = expenseRepository.findAll();
        }

        BigDecimal total = expenses.stream()
                .map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<Expense>> catMap = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getCategoryName() != null ? e.getCategoryName() : "General"));

        List<ExpenseReportDTO.CategoryExpenseBreakdown> breakdown = new ArrayList<>();
        catMap.forEach((catName, list) -> {
            BigDecimal catTotal = list.stream().map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            breakdown.add(ExpenseReportDTO.CategoryExpenseBreakdown.builder()
                    .categoryId(list.get(0).getCategoryId())
                    .categoryName(catName)
                    .totalAmount(catTotal)
                    .count((long) list.size())
                    .build());
        });

        List<ExpenseDTO> dtoList = expenses.stream().map(e -> ExpenseDTO.builder()
                .id(e.getId())
                .expenseNumber(e.getExpenseNumber())
                .categoryId(e.getCategoryId())
                .categoryName(e.getCategoryName())
                .title(e.getTitle())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .vendorId(e.getVendorId())
                .vendorName(e.getVendorName())
                .employeeId(e.getEmployeeId())
                .approvalStatus(e.getApprovalStatus())
                .build()).collect(Collectors.toList());

        return ExpenseReportDTO.builder()
                .financialYearId(financialYearId)
                .totalExpenses(total)
                .categoryBreakdown(breakdown)
                .expenseList(dtoList)
                .build();
    }

    @Override
    public RevenueReportDTO getRevenueReport(String financialYearId, LocalDate startDate, LocalDate endDate) {
        List<Invoice> invoices;
        if (startDate != null && endDate != null) {
            invoices = invoiceRepository.findByIssueDateBetween(startDate, endDate);
        } else {
            invoices = invoiceRepository.findAll();
        }

        BigDecimal total = invoices.stream()
                .map(i -> i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<Invoice>> custMap = invoices.stream()
                .collect(Collectors.groupingBy(i -> i.getCustomerName() != null ? i.getCustomerName() : "General Customer"));

        List<RevenueReportDTO.CustomerRevenueBreakdown> breakdown = new ArrayList<>();
        custMap.forEach((custName, list) -> {
            BigDecimal custTotal = list.stream().map(i -> i.getTotalAmount() != null ? i.getTotalAmount() : BigDecimal.ZERO).reduce(BigDecimal.ZERO, BigDecimal::add);
            breakdown.add(RevenueReportDTO.CustomerRevenueBreakdown.builder()
                    .customerId(list.get(0).getCustomerId())
                    .customerName(custName)
                    .totalRevenue(custTotal)
                    .invoiceCount((long) list.size())
                    .build());
        });

        List<InvoiceDTO> dtoList = invoices.stream().map(i -> InvoiceDTO.builder()
                .id(i.getId())
                .invoiceNumber(i.getInvoiceNumber())
                .customerId(i.getCustomerId())
                .customerName(i.getCustomerName())
                .totalAmount(i.getTotalAmount())
                .paidAmount(i.getPaidAmount())
                .balanceDue(i.getBalanceDue())
                .status(i.getStatus())
                .issueDate(i.getIssueDate())
                .build()).collect(Collectors.toList());

        return RevenueReportDTO.builder()
                .financialYearId(financialYearId)
                .totalRevenue(total)
                .customerBreakdown(breakdown)
                .invoiceList(dtoList)
                .build();
    }

    @Override
    public VendorReportDTO getVendorReport() {
        List<Vendor> vendors = vendorRepository.findAll();

        BigDecimal totalPurchasesAll = BigDecimal.ZERO;
        BigDecimal totalOutstandingAll = BigDecimal.ZERO;

        List<VendorReportDTO.VendorSummary> summaries = new ArrayList<>();
        for (Vendor v : vendors) {
            BigDecimal purchases = v.getTotalPurchases() != null ? v.getTotalPurchases() : BigDecimal.ZERO;
            BigDecimal outstanding = v.getOutstandingBalance() != null ? v.getOutstandingBalance() : BigDecimal.ZERO;

            summaries.add(VendorReportDTO.VendorSummary.builder()
                    .vendorId(v.getId())
                    .vendorCode(v.getVendorCode())
                    .vendorName(v.getVendorName())
                    .totalPurchases(purchases)
                    .outstandingBalance(outstanding)
                    .poCount(0L)
                    .build());

            totalPurchasesAll = totalPurchasesAll.add(purchases);
            totalOutstandingAll = totalOutstandingAll.add(outstanding);
        }

        return VendorReportDTO.builder()
                .vendors(summaries)
                .totalPurchasesAllVendors(totalPurchasesAll)
                .totalOutstandingAllVendors(totalOutstandingAll)
                .build();
    }

    @Override
    public InvoiceAgingReportDTO getInvoiceAgingReport() {
        List<Invoice> unpaidInvoices = invoiceRepository.findAll().stream()
                .filter(i -> i.getBalanceDue() != null && i.getBalanceDue().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        Map<String, InvoiceAgingReportDTO.CustomerAgingSummary> custMap = new HashMap<>();

        BigDecimal totalCurrent = BigDecimal.ZERO;
        BigDecimal total30To60 = BigDecimal.ZERO;
        BigDecimal total60To90 = BigDecimal.ZERO;
        BigDecimal totalOver90 = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        LocalDate today = LocalDate.now();

        for (Invoice inv : unpaidInvoices) {
            String custId = inv.getCustomerId() != null ? inv.getCustomerId() : "UNKNOWN";
            String custName = inv.getCustomerName() != null ? inv.getCustomerName() : "Unknown Customer";

            long daysOverdue = inv.getDueDate() != null ? ChronoUnit.DAYS.between(inv.getDueDate(), today) : 0;
            BigDecimal balance = inv.getBalanceDue();

            InvoiceAgingReportDTO.CustomerAgingSummary summary = custMap.computeIfAbsent(custId, k ->
                    InvoiceAgingReportDTO.CustomerAgingSummary.builder()
                            .customerId(custId)
                            .customerName(custName)
                            .currentAmount(BigDecimal.ZERO)
                            .days30To60(BigDecimal.ZERO)
                            .days60To90(BigDecimal.ZERO)
                            .over90Days(BigDecimal.ZERO)
                            .totalOutstanding(BigDecimal.ZERO)
                            .build());

            if (daysOverdue <= 30) {
                summary.setCurrentAmount(summary.getCurrentAmount().add(balance));
                totalCurrent = totalCurrent.add(balance);
            } else if (daysOverdue <= 60) {
                summary.setDays30To60(summary.getDays30To60().add(balance));
                total30To60 = total30To60.add(balance);
            } else if (daysOverdue <= 90) {
                summary.setDays60To90(summary.getDays60To90().add(balance));
                total60To90 = total60To90.add(balance);
            } else {
                summary.setOver90Days(summary.getOver90Days().add(balance));
                totalOver90 = totalOver90.add(balance);
            }

            summary.setTotalOutstanding(summary.getTotalOutstanding().add(balance));
            grandTotal = grandTotal.add(balance);
        }

        return InvoiceAgingReportDTO.builder()
                .agingSummaries(new ArrayList<>(custMap.values()))
                .totalCurrent(totalCurrent)
                .total30To60(total30To60)
                .total60To90(total60To90)
                .totalOver90(totalOver90)
                .grandTotalOutstanding(grandTotal)
                .build();
    }

    @Override
    public BudgetVarianceReportDTO getBudgetVarianceReport(String financialYearId) {
        List<Budget> budgets;
        if (financialYearId != null) {
            budgets = budgetRepository.findByFinancialYearId(financialYearId);
        } else {
            budgets = budgetRepository.findAll();
        }

        BigDecimal totalBudgeted = BigDecimal.ZERO;
        BigDecimal totalActual = BigDecimal.ZERO;
        BigDecimal totalVariance = BigDecimal.ZERO;

        List<BudgetVarianceReportDTO.BudgetVarianceRow> rows = new ArrayList<>();
        for (Budget b : budgets) {
            BigDecimal budgeted = b.getBudgetedAmount() != null ? b.getBudgetedAmount() : BigDecimal.ZERO;
            BigDecimal actual = b.getActualAmount() != null ? b.getActualAmount() : BigDecimal.ZERO;
            BigDecimal variance = budgeted.subtract(actual);

            double variancePct = 0.0;
            if (budgeted.compareTo(BigDecimal.ZERO) > 0) {
                variancePct = variance.divide(budgeted, 4, RoundingMode.HALF_UP).doubleValue() * 100;
            }

            rows.add(BudgetVarianceReportDTO.BudgetVarianceRow.builder()
                    .budgetId(b.getId())
                    .budgetName(b.getBudgetName())
                    .budgetScope(b.getBudgetScope())
                    .departmentOrProject(b.getDepartmentId() != null ? b.getDepartmentId() : b.getProjectId())
                    .budgetedAmount(budgeted)
                    .actualAmount(actual)
                    .varianceAmount(variance)
                    .variancePercentage(variancePct)
                    .build());

            totalBudgeted = totalBudgeted.add(budgeted);
            totalActual = totalActual.add(actual);
            totalVariance = totalVariance.add(variance);
        }

        return BudgetVarianceReportDTO.builder()
                .financialYearId(financialYearId)
                .budgetRows(rows)
                .totalBudgeted(totalBudgeted)
                .totalActual(totalActual)
                .totalVariance(totalVariance)
                .build();
    }
}
