package com.techknife.analytics.controller;

import com.techknife.analytics.dto.TrendAnalysisDTO;
import com.techknife.analytics.entity.KPICategory;
import com.techknife.analytics.entity.TrendPeriod;
import com.techknife.analytics.service.TrendAnalysisService;
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
@RequestMapping("/api/v1/analytics/trends")
@RequiredArgsConstructor
@Tag(name = "Analytics - Trend Analysis", description = "Trend Analysis & Growth Metrics API")
@SecurityRequirement(name = "bearerAuth")
public class TrendAnalysisController {

    private final TrendAnalysisService trendAnalysisService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Trend Analyses")
    public ResponseEntity<ApiResponse<List<TrendAnalysisDTO>>> getAllTrendAnalyses() {
        List<TrendAnalysisDTO> list = trendAnalysisService.getAllTrendAnalyses();
        return ResponseEntity.ok(ApiResponse.success(list, "Trend analyses retrieved successfully"));
    }

    @GetMapping("/{metricKey}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Trend Analysis for Metric")
    public ResponseEntity<ApiResponse<TrendAnalysisDTO>> getTrendAnalysis(
            @PathVariable String metricKey,
            @RequestParam(required = false, defaultValue = "MONTHLY") TrendPeriod period) {
        TrendAnalysisDTO trend = trendAnalysisService.getTrendAnalysisByMetricAndPeriod(metricKey, period);
        return ResponseEntity.ok(ApiResponse.success(trend, "Trend analysis retrieved successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_MANAGE', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Generate Trend Analysis Dataset")
    public ResponseEntity<ApiResponse<TrendAnalysisDTO>> generateTrendAnalysis(
            @RequestParam String metricKey,
            @RequestParam(required = false, defaultValue = "ORG_GROWTH") KPICategory category,
            @RequestParam(required = false, defaultValue = "MONTHLY") TrendPeriod period) {
        TrendAnalysisDTO trend = trendAnalysisService.generateTrendAnalysis(metricKey, category, period);
        return ResponseEntity.ok(ApiResponse.success(trend, "Trend analysis generated successfully"));
    }
}
