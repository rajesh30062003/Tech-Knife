package com.techknife.analytics.controller;

import com.techknife.analytics.dto.BusinessInsightDTO;
import com.techknife.analytics.entity.InsightSeverity;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.service.BusinessInsightService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics/insights")
@RequiredArgsConstructor
@Tag(name = "Analytics - Business Insights", description = "Automated Business Insights & Risk Alerts API")
@SecurityRequirement(name = "bearerAuth")
public class BusinessInsightController {

    private final BusinessInsightService businessInsightService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Business Insights")
    public ResponseEntity<ApiResponse<List<BusinessInsightDTO>>> getAllInsights() {
        List<BusinessInsightDTO> insights = businessInsightService.getAllInsights();
        return ResponseEntity.ok(ApiResponse.success(insights, "Insights retrieved successfully"));
    }

    @GetMapping("/severity/{severity}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Insights by Severity")
    public ResponseEntity<ApiResponse<List<BusinessInsightDTO>>> getInsightsBySeverity(@PathVariable InsightSeverity severity) {
        List<BusinessInsightDTO> insights = businessInsightService.getInsightsBySeverity(severity);
        return ResponseEntity.ok(ApiResponse.success(insights, "Insights retrieved successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Insights by Category")
    public ResponseEntity<ApiResponse<List<BusinessInsightDTO>>> getInsightsByCategory(@PathVariable KPICategory category) {
        List<BusinessInsightDTO> insights = businessInsightService.getInsightsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(insights, "Category insights retrieved successfully"));
    }

    @PostMapping("/{id}/acknowledge")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_MANAGE', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "BusinessInsight", description = "Business Insight Acknowledged")
    @Operation(summary = "Acknowledge Business Insight Alert")
    public ResponseEntity<ApiResponse<BusinessInsightDTO>> acknowledgeInsight(@PathVariable String id) {
        BusinessInsightDTO acknowledged = businessInsightService.acknowledgeInsight(id);
        return ResponseEntity.ok(ApiResponse.success(acknowledged, "Insight acknowledged successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_MANAGE', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ANALYTICS, entityType = "BusinessInsight", description = "Generated Business Insights")
    @Operation(summary = "Generate Fresh Business Insights")
    public ResponseEntity<ApiResponse<List<BusinessInsightDTO>>> generateInsights() {
        List<BusinessInsightDTO> insights = businessInsightService.generateInsights();
        return ResponseEntity.ok(ApiResponse.success(insights, "Business insights generated successfully"));
    }
}
