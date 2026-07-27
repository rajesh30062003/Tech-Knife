package com.techknife.project.analytics.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.analytics.dto.ProductivityAnalyticsDTO;
import com.techknife.project.analytics.service.ProductivityAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/project/analytics")
@RequiredArgsConstructor
@Tag(name = "Productivity Analytics", description = "Endpoints for Project Velocity, Cycle Times, Task Completion Rates, and Productivity Hours")
@SecurityRequirement(name = "bearerAuth")
public class ProductivityAnalyticsController {

    private final ProductivityAnalyticsService productivityAnalyticsService;

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Comprehensive Productivity Analytics for Project")
    public ResponseEntity<ApiResponse<ProductivityAnalyticsDTO>> getProductivityAnalytics(@PathVariable String projectId) {
        ProductivityAnalyticsDTO dto = productivityAnalyticsService.getProductivityAnalytics(projectId);
        return ResponseEntity.ok(ApiResponse.success(dto, "Productivity analytics retrieved successfully"));
    }
}
