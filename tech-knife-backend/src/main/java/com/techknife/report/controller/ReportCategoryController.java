package com.techknife.report.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ReportCategoryDTO;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.service.ReportCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/report-categories")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Categories", description = "Report Categories API")
@SecurityRequirement(name = "bearerAuth")
public class ReportCategoryController {

    private final ReportCategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_TEMPLATE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create Report Category")
    public ResponseEntity<ApiResponse<ReportCategoryDTO>> createCategory(@Valid @RequestBody ReportCategoryDTO dto) {
        ReportCategoryDTO result = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created category successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Report Categories")
    public ResponseEntity<ApiResponse<List<ReportCategoryDTO>>> getAllCategories() {
        List<ReportCategoryDTO> result = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched categories successfully"));
    }

    @GetMapping("/{type}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Category by Type")
    public ResponseEntity<ApiResponse<ReportCategoryDTO>> getCategoryByType(@PathVariable ReportCategoryType type) {
        ReportCategoryDTO result = categoryService.getCategoryByType(type);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched category successfully"));
    }
}
