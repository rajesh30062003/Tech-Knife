package com.techknife.organization.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.DesignationRequest;
import com.techknife.organization.dto.DesignationResponse;
import com.techknife.organization.service.DesignationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization/designations")
@RequiredArgsConstructor
@Auditable(module = "Designation Management")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Designation API", description = "Endpoints for managing Job Designations / Role Titles")
public class DesignationController {

    private final DesignationService designationService;

    @PostMapping
    @PreAuthorize("hasAuthority('DESIGNATION_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Create designation", description = "Registers a new designation.")
    public ResponseEntity<ApiResponse<DesignationResponse>> createDesignation(@Valid @RequestBody DesignationRequest request) {
        DesignationResponse response = designationService.createDesignation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Designation created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DESIGNATION_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Update designation", description = "Updates details of an existing designation.")
    public ResponseEntity<ApiResponse<DesignationResponse>> updateDesignation(
            @Parameter(description = "Designation ID") @PathVariable("id") String id,
            @Valid @RequestBody DesignationRequest request) {
        DesignationResponse response = designationService.updateDesignation(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DESIGNATION_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get designation by ID", description = "Retrieves designation details by ID.")
    public ResponseEntity<ApiResponse<DesignationResponse>> getDesignationById(
            @Parameter(description = "Designation ID") @PathVariable("id") String id) {
        DesignationResponse response = designationService.getDesignationById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('DESIGNATION_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get designation by Code", description = "Retrieves designation details by code.")
    public ResponseEntity<ApiResponse<DesignationResponse>> getDesignationByCode(
            @Parameter(description = "Designation Code") @PathVariable("code") String code) {
        DesignationResponse response = designationService.getDesignationByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Designation retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DESIGNATION_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all designations", description = "Retrieves paginated list of designations.")
    public ResponseEntity<ApiResponse<PagedResponse<DesignationResponse>>> getAllDesignations(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<DesignationResponse> response = designationService.getAllDesignations(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Designations list retrieved successfully"));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('DESIGNATION_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get designations by department", description = "Lists designations for a department.")
    public ResponseEntity<ApiResponse<List<DesignationResponse>>> getDesignationsByDepartment(
            @Parameter(description = "Department ID") @PathVariable("departmentId") String departmentId) {
        List<DesignationResponse> response = designationService.getDesignationsByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Designations list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DESIGNATION_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete designation", description = "Removes a designation record.")
    public ResponseEntity<ApiResponse<Void>> deleteDesignation(
            @Parameter(description = "Designation ID") @PathVariable("id") String id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Designation deleted successfully"));
    }
}
