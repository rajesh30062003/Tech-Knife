package com.techknife.analytics.controller;

import com.techknife.analytics.dto.ForecastDatasetDTO;
import com.techknife.analytics.entity.ForecastType;
import com.techknife.analytics.entity.TrendPeriod;
import com.techknife.analytics.service.ForecastDatasetService;
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
@RequestMapping("/api/v1/analytics/forecasts")
@RequiredArgsConstructor
@Tag(name = "Analytics - Forecast Datasets", description = "Forecast-ready Structured Datasets API")
@SecurityRequirement(name = "bearerAuth")
public class ForecastDatasetController {

    private final ForecastDatasetService forecastDatasetService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Forecast Datasets")
    public ResponseEntity<ApiResponse<List<ForecastDatasetDTO>>> getAllForecastDatasets() {
        List<ForecastDatasetDTO> list = forecastDatasetService.getAllForecastDatasets();
        return ResponseEntity.ok(ApiResponse.success(list, "Forecast datasets retrieved successfully"));
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_VIEW', 'KPI_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Forecast Dataset by Type")
    public ResponseEntity<ApiResponse<ForecastDatasetDTO>> getForecastDatasetByType(@PathVariable ForecastType type) {
        ForecastDatasetDTO dataset = forecastDatasetService.getForecastDatasetByType(type);
        return ResponseEntity.ok(ApiResponse.success(dataset, "Forecast dataset retrieved successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ANALYTICS_MANAGE', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Generate Structured Forecast Dataset")
    public ResponseEntity<ApiResponse<ForecastDatasetDTO>> generateForecastDataset(
            @RequestParam ForecastType forecastType,
            @RequestParam(required = false, defaultValue = "MONTHLY") TrendPeriod granularity,
            @RequestParam(required = false, defaultValue = "6") int forecastPeriods) {
        ForecastDatasetDTO dataset = forecastDatasetService.generateForecastDataset(forecastType, granularity, forecastPeriods);
        return ResponseEntity.ok(ApiResponse.success(dataset, "Forecast dataset generated successfully"));
    }
}
