package com.techknife.finance.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.*;
import com.techknife.finance.service.FinanceReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/finance/reports")
@RequiredArgsConstructor
@Tag(name = "Finance - Financial Reports", description = "Generate General Ledger, Trial Balance, P&L, Balance Sheet, Cash Flow, Expense, Revenue, Vendor, Invoice Aging, and Budget Variance Reports")
@SecurityRequirement(name = "bearerAuth")
public class FinanceReportController {

    private final FinanceReportService financeReportService;

    @GetMapping("/general-ledger")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get General Ledger Report")
    public ResponseEntity<ApiResponse<GeneralLedgerReportDTO>> getGeneralLedgerReport(
            @RequestParam String accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        GeneralLedgerReportDTO report = financeReportService.getGeneralLedgerReport(accountId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated general ledger report successfully"));
    }

    @GetMapping("/trial-balance")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Trial Balance Report")
    public ResponseEntity<ApiResponse<TrialBalanceReportDTO>> getTrialBalanceReport(@RequestParam(required = false) String financialYearId) {
        TrialBalanceReportDTO report = financeReportService.getTrialBalanceReport(financialYearId);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated trial balance report successfully"));
    }

    @GetMapping("/profit-and-loss")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Profit & Loss Report")
    public ResponseEntity<ApiResponse<ProfitAndLossReportDTO>> getProfitAndLossReport(
            @RequestParam(required = false) String financialYearId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ProfitAndLossReportDTO report = financeReportService.getProfitAndLossReport(financialYearId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated profit & loss report successfully"));
    }

    @GetMapping("/balance-sheet")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Balance Sheet Report")
    public ResponseEntity<ApiResponse<BalanceSheetReportDTO>> getBalanceSheetReport(@RequestParam(required = false) String financialYearId) {
        BalanceSheetReportDTO report = financeReportService.getBalanceSheetReport(financialYearId);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated balance sheet report successfully"));
    }

    @GetMapping("/cash-flow")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Cash Flow Report")
    public ResponseEntity<ApiResponse<CashFlowReportDTO>> getCashFlowReport(@RequestParam(required = false) String financialYearId) {
        CashFlowReportDTO report = financeReportService.getCashFlowReport(financialYearId);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated cash flow report successfully"));
    }

    @GetMapping("/expense")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Expense Report")
    public ResponseEntity<ApiResponse<ExpenseReportDTO>> getExpenseReport(
            @RequestParam(required = false) String financialYearId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ExpenseReportDTO report = financeReportService.getExpenseReport(financialYearId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated expense report successfully"));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Revenue Report")
    public ResponseEntity<ApiResponse<RevenueReportDTO>> getRevenueReport(
            @RequestParam(required = false) String financialYearId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        RevenueReportDTO report = financeReportService.getRevenueReport(financialYearId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated revenue report successfully"));
    }

    @GetMapping("/vendor")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Vendor Report")
    public ResponseEntity<ApiResponse<VendorReportDTO>> getVendorReport() {
        VendorReportDTO report = financeReportService.getVendorReport();
        return ResponseEntity.ok(ApiResponse.success(report, "Generated vendor report successfully"));
    }

    @GetMapping("/invoice-aging")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Invoice Aging Report")
    public ResponseEntity<ApiResponse<InvoiceAgingReportDTO>> getInvoiceAgingReport() {
        InvoiceAgingReportDTO report = financeReportService.getInvoiceAgingReport();
        return ResponseEntity.ok(ApiResponse.success(report, "Generated invoice aging report successfully"));
    }

    @GetMapping("/budget-variance")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Budget Variance Report")
    public ResponseEntity<ApiResponse<BudgetVarianceReportDTO>> getBudgetVarianceReport(@RequestParam(required = false) String financialYearId) {
        BudgetVarianceReportDTO report = financeReportService.getBudgetVarianceReport(financialYearId);
        return ResponseEntity.ok(ApiResponse.success(report, "Generated budget variance report successfully"));
    }
}
