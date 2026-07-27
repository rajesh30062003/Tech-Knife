package com.techknife.report.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.report.dto.ReportTemplateDTO;
import com.techknife.report.entity.ReportCategoryType;
import com.techknife.report.service.ReportTemplateService;
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
@RequestMapping("/api/v1/report-templates")
@RequiredArgsConstructor
@Tag(name = "Report Engine - Templates", description = "Reusable Report Templates API")
@SecurityRequirement(name = "bearerAuth")
public class ReportTemplateController {

    private final ReportTemplateService templateService;

    @PostMapping
    @PreAuthorize("hasAuthority('REPORT_TEMPLATE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.REPORT, entityType = "ReportTemplate", description = "Created Template")
    @Operation(summary = "Create Reusable Report Template")
    public ResponseEntity<ApiResponse<ReportTemplateDTO>> createTemplate(@Valid @RequestBody ReportTemplateDTO dto) {
        ReportTemplateDTO result = templateService.createTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Report template created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_TEMPLATE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.REPORT, entityType = "ReportTemplate", description = "Updated Template")
    @Operation(summary = "Update Report Template")
    public ResponseEntity<ApiResponse<ReportTemplateDTO>> updateTemplate(
            @PathVariable String id,
            @Valid @RequestBody ReportTemplateDTO dto) {
        ReportTemplateDTO result = templateService.updateTemplate(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Report template updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Template by ID")
    public ResponseEntity<ApiResponse<ReportTemplateDTO>> getTemplateById(@PathVariable String id) {
        ReportTemplateDTO result = templateService.getTemplateById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched template successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Template by Code")
    public ResponseEntity<ApiResponse<ReportTemplateDTO>> getTemplateByCode(@PathVariable String code) {
        ReportTemplateDTO result = templateService.getTemplateByCode(code);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched template by code successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Templates")
    public ResponseEntity<ApiResponse<List<ReportTemplateDTO>>> getAllTemplates() {
        List<ReportTemplateDTO> result = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all templates successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Templates by Category")
    public ResponseEntity<ApiResponse<List<ReportTemplateDTO>>> getTemplatesByCategory(@PathVariable ReportCategoryType category) {
        List<ReportTemplateDTO> result = templateService.getTemplatesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched templates by category successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search Templates")
    public ResponseEntity<ApiResponse<List<ReportTemplateDTO>>> searchTemplates(@RequestParam(required = false) String query) {
        List<ReportTemplateDTO> result = templateService.searchTemplates(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Searched templates successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REPORT_TEMPLATE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.REPORT, entityType = "ReportTemplate", description = "Deleted Template")
    @Operation(summary = "Delete Template")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted template successfully"));
    }
}
