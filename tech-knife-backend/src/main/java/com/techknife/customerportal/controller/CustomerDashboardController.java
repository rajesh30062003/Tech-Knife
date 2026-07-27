package com.techknife.customerportal.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerAnalyticsDTO;
import com.techknife.customerportal.dto.CustomerDashboardDTO;
import com.techknife.customerportal.service.CustomerDashboardService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/customer/dashboard")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Dashboard & Analytics", description = "Aggregated Dashboard Metrics and Analytics")
@SecurityRequirement(name = "bearerAuth")
public class CustomerDashboardController {

    private final CustomerDashboardService customerDashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Dashboard Summary")
    public ResponseEntity<ApiResponse<CustomerDashboardDTO>> getDashboard(@CurrentUser UserPrincipal userPrincipal) {
        CustomerDashboardDTO result = customerDashboardService.getDashboard(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Fetched dashboard metrics successfully", result));
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Dashboard Analytics")
    public ResponseEntity<ApiResponse<CustomerAnalyticsDTO>> getAnalytics(@CurrentUser UserPrincipal userPrincipal) {
        CustomerAnalyticsDTO result = customerDashboardService.getAnalytics(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Fetched customer analytics successfully", result));
    }
}
