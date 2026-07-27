package com.techknife.crm.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.CrmDashboardDTO;
import com.techknife.crm.service.CrmDashboardService;
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
@RequestMapping("/api/v1/crm/dashboard")
@RequiredArgsConstructor
@Tag(name = "CRM - Dashboard", description = "Endpoints for CRM Dashboard Summaries")
@SecurityRequirement(name = "bearerAuth")
public class CrmDashboardController {

    private final CrmDashboardService crmDashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('CRM_ANALYTICS_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get CRM dashboard metrics")
    public ResponseEntity<ApiResponse<CrmDashboardDTO>> getDashboard() {
        CrmDashboardDTO result = crmDashboardService.getDashboardData();
        return ResponseEntity.ok(ApiResponse.success("Fetched CRM dashboard metrics successfully", result));
    }
}
