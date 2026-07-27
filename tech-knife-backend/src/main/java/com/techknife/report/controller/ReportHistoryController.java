package com.techknife.report.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ReportHistoryDTO;
import com.techknife.report.service.ReportHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-histories")
@RequiredArgsConstructor
@Tag(name = "Report Engine - History", description = "Report Execution & Generation Audit History API")
@SecurityRequirement(name = "bearerAuth")
public class ReportHistoryController {

    private final ReportHistoryService historyService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Report History Record by ID")
    public ResponseEntity<ApiResponse<ReportHistoryDTO>> getHistoryById(@PathVariable String id) {
        ReportHistoryDTO result = historyService.getHistoryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report history record successfully"));
    }

    @GetMapping("/report/{reportId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get History Records by Report ID")
    public ResponseEntity<ApiResponse<List<ReportHistoryDTO>>> getHistoryByReport(@PathVariable String reportId) {
        List<ReportHistoryDTO> result = historyService.getHistoryByReport(reportId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched report histories successfully"));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get History Records by User ID")
    public ResponseEntity<ApiResponse<List<ReportHistoryDTO>>> getHistoryByUser(@PathVariable String userId) {
        List<ReportHistoryDTO> result = historyService.getHistoryByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user report histories successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search Report History")
    public ResponseEntity<ApiResponse<List<ReportHistoryDTO>>> searchHistory(@RequestParam(required = false) String query) {
        List<ReportHistoryDTO> result = historyService.searchHistory(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Searched report histories successfully"));
    }
}
