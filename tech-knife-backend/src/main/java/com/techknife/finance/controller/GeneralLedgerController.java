package com.techknife.finance.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.GeneralLedgerDTO;
import com.techknife.finance.service.GeneralLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/general-ledger")
@RequiredArgsConstructor
@Tag(name = "Finance - General Ledger", description = "View General Ledger entries and account postings")
@SecurityRequirement(name = "bearerAuth")
public class GeneralLedgerController {

    private final GeneralLedgerService generalLedgerService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All General Ledger Entries")
    public ResponseEntity<ApiResponse<List<GeneralLedgerDTO>>> getAllLedgerEntries(
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String financialYearId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<GeneralLedgerDTO> result;
        if (accountId != null && startDate != null && endDate != null) {
            result = generalLedgerService.getLedgerEntriesByAccountAndDateRange(accountId, startDate, endDate);
        } else if (accountId != null) {
            result = generalLedgerService.getLedgerEntriesByAccount(accountId);
        } else if (financialYearId != null) {
            result = generalLedgerService.getLedgerEntriesByFinancialYear(financialYearId);
        } else {
            result = generalLedgerService.getAllLedgerEntries();
        }

        return ResponseEntity.ok(ApiResponse.success(result, "Fetched general ledger entries successfully"));
    }
}
