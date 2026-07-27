package com.techknife.finance.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.FinanceDashboardDTO;
import com.techknife.finance.service.FinanceDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finance/dashboard")
@RequiredArgsConstructor
@Tag(name = "Finance - Dashboard", description = "Executive Dashboard with Financial Overview and KPIs")
@SecurityRequirement(name = "bearerAuth")
public class FinanceDashboardController {

    private final FinanceDashboardService financeDashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Finance Executive Dashboard")
    public ResponseEntity<ApiResponse<FinanceDashboardDTO>> getDashboardData() {
        FinanceDashboardDTO dashboard = financeDashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success(dashboard, "Fetched finance dashboard successfully"));
    }
}
