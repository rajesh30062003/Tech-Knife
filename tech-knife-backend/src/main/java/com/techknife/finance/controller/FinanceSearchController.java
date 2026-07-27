package com.techknife.finance.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.FinanceSearchResultDTO;
import com.techknife.finance.service.FinanceSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/finance/search")
@RequiredArgsConstructor
@Tag(name = "Finance - Search", description = "Global Search across Invoices, Vendors, Expenses, Journals, Ledgers, Cost Centers, and Financial Years")
@SecurityRequirement(name = "bearerAuth")
public class FinanceSearchController {

    private final FinanceSearchService financeSearchService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Global Search Finance Records")
    public ResponseEntity<ApiResponse<FinanceSearchResultDTO>> searchFinanceRecords(@RequestParam String query) {
        FinanceSearchResultDTO result = financeSearchService.searchFinanceRecords(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Executed finance search successfully"));
    }
}
