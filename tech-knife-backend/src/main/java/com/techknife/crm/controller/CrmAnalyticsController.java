package com.techknife.crm.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.CrmAnalyticsDTO;
import com.techknife.crm.service.CrmAnalyticsService;
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
@RequestMapping("/api/v1/crm/analytics")
@RequiredArgsConstructor
@Tag(name = "CRM - Analytics", description = "Endpoints for CRM Performance and Sales Analytics")
@SecurityRequirement(name = "bearerAuth")
public class CrmAnalyticsController {

    private final CrmAnalyticsService crmAnalyticsService;

    @GetMapping
    @PreAuthorize("hasAuthority('CRM_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get CRM sales performance and conversion analytics")
    public ResponseEntity<ApiResponse<CrmAnalyticsDTO>> getAnalytics() {
        CrmAnalyticsDTO result = crmAnalyticsService.getAnalyticsData();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched CRM analytics successfully"));
    }

}
