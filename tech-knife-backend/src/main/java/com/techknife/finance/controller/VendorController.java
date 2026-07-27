package com.techknife.finance.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.finance.dto.VendorDTO;
import com.techknife.finance.service.VendorService;
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
@RequestMapping("/api/v1/finance/vendors")
@RequiredArgsConstructor
@Tag(name = "Finance - Vendors", description = "Manage Vendors and Vendor Profiles")
@SecurityRequirement(name = "bearerAuth")
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Vendors")
    public ResponseEntity<ApiResponse<List<VendorDTO>>> getAllVendors() {
        List<VendorDTO> result = vendorService.getAllVendors();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched vendors successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_VIEW') or hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Vendor by ID")
    public ResponseEntity<ApiResponse<VendorDTO>> getVendorById(@PathVariable String id) {
        VendorDTO result = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched vendor successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.FINANCE, entityType = "Vendor", description = "Created Vendor")
    @Operation(summary = "Create Vendor")
    public ResponseEntity<ApiResponse<VendorDTO>> createVendor(@Valid @RequestBody VendorDTO dto) {
        VendorDTO result = vendorService.createVendor(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created vendor successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.FINANCE, entityType = "Vendor", description = "Updated Vendor")
    @Operation(summary = "Update Vendor")
    public ResponseEntity<ApiResponse<VendorDTO>> updateVendor(@PathVariable String id, @Valid @RequestBody VendorDTO dto) {
        VendorDTO result = vendorService.updateVendor(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated vendor successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.FINANCE, entityType = "Vendor", description = "Deleted Vendor")
    @Operation(summary = "Delete Vendor")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable String id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted vendor successfully"));
    }
}
