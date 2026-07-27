package com.techknife.asset.controller;

import com.techknife.asset.dto.LicenseAssignmentDTO;
import com.techknife.asset.dto.SoftwareLicenseDTO;
import com.techknife.asset.service.SoftwareLicenseService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
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
@RequestMapping("/api/v1/assets/licenses")
@RequiredArgsConstructor
@Tag(name = "Asset - Software Licenses", description = "Software License Management API")
@SecurityRequirement(name = "bearerAuth")
public class SoftwareLicenseController {

    private final SoftwareLicenseService licenseService;

    @GetMapping
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Software Licenses")
    public ResponseEntity<ApiResponse<List<SoftwareLicenseDTO>>> getAllLicenses() {
        List<SoftwareLicenseDTO> result = licenseService.getAllLicenses();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched all software licenses"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Software License by ID")
    public ResponseEntity<ApiResponse<SoftwareLicenseDTO>> getLicenseById(@PathVariable String id) {
        SoftwareLicenseDTO result = licenseService.getLicenseById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched software license successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.ASSET, entityType = "SoftwareLicense", description = "Created Software License")
    @Operation(summary = "Create Software License")
    public ResponseEntity<ApiResponse<SoftwareLicenseDTO>> createLicense(@Valid @RequestBody SoftwareLicenseDTO dto) {
        SoftwareLicenseDTO result = licenseService.createLicense(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created software license successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ASSET, entityType = "SoftwareLicense", description = "Updated Software License")
    @Operation(summary = "Update Software License")
    public ResponseEntity<ApiResponse<SoftwareLicenseDTO>> updateLicense(@PathVariable String id, @Valid @RequestBody SoftwareLicenseDTO dto) {
        SoftwareLicenseDTO result = licenseService.updateLicense(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated software license successfully"));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.ASSIGN, module = AuditModule.ASSET, entityType = "SoftwareLicense", description = "Assigned Software License")
    @Operation(summary = "Assign Software License to Employee")
    public ResponseEntity<ApiResponse<LicenseAssignmentDTO>> assignLicense(
            @PathVariable String id,
            @RequestParam String employeeId,
            @RequestParam(required = false) String employeeName) {
        LicenseAssignmentDTO result = licenseService.assignLicense(id, employeeId, employeeName);
        return ResponseEntity.ok(ApiResponse.success(result, "Assigned software license successfully"));
    }

    @PostMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UNASSIGN, module = AuditModule.ASSET, entityType = "SoftwareLicense", description = "Revoked Software License")
    @Operation(summary = "Revoke Software License from Employee")
    public ResponseEntity<ApiResponse<LicenseAssignmentDTO>> revokeLicense(
            @PathVariable String id,
            @RequestParam String employeeId) {
        LicenseAssignmentDTO result = licenseService.revokeLicense(id, employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Revoked software license successfully"));
    }

    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasAuthority('LICENSE_MANAGE') or hasAuthority('ASSET_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Software License Assignments")
    public ResponseEntity<ApiResponse<List<LicenseAssignmentDTO>>> getLicenseAssignments(@PathVariable String id) {
        List<LicenseAssignmentDTO> result = licenseService.getLicenseAssignments(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched software license assignments"));
    }
}
