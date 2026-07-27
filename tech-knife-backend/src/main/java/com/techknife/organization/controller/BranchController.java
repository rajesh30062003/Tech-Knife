package com.techknife.organization.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.BranchRequest;
import com.techknife.organization.dto.BranchResponse;
import com.techknife.organization.service.BranchService;
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
@RequestMapping("/api/v1/organization/branches")
@RequiredArgsConstructor
@Auditable(module = "Branch Management")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Branch API", description = "Endpoints for managing Physical or Logical Branches")
public class BranchController {

    private final BranchService branchService;

    @PostMapping
    @PreAuthorize("hasAuthority('BRANCH_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Create branch", description = "Registers a new branch office.")
    public ResponseEntity<ApiResponse<BranchResponse>> createBranch(@Valid @RequestBody BranchRequest request) {
        BranchResponse response = branchService.createBranch(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Branch created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BRANCH_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Update branch", description = "Updates details of an existing branch.")
    public ResponseEntity<ApiResponse<BranchResponse>> updateBranch(
            @Parameter(description = "Branch ID") @PathVariable("id") String id,
            @Valid @RequestBody BranchRequest request) {
        BranchResponse response = branchService.updateBranch(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Branch updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BRANCH_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get branch by ID", description = "Retrieves branch details by ID.")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchById(
            @Parameter(description = "Branch ID") @PathVariable("id") String id) {
        BranchResponse response = branchService.getBranchById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Branch retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('BRANCH_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get branch by Code", description = "Retrieves branch details by code.")
    public ResponseEntity<ApiResponse<BranchResponse>> getBranchByCode(
            @Parameter(description = "Branch Code") @PathVariable("code") String code) {
        BranchResponse response = branchService.getBranchByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Branch retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BRANCH_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all branches", description = "Retrieves paginated list of branches.")
    public ResponseEntity<ApiResponse<PagedResponse<BranchResponse>>> getAllBranches(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "companyId", required = false) String companyId) {
        PagedResponse<BranchResponse> response = branchService.getAllBranches(page, size, companyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Branches list retrieved successfully"));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAuthority('BRANCH_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get branches by company", description = "Lists all branches for a given company.")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> getBranchesByCompany(
            @Parameter(description = "Company ID") @PathVariable("companyId") String companyId) {
        List<BranchResponse> response = branchService.getBranchesByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Branches list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BRANCH_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete branch", description = "Removes a branch record.")
    public ResponseEntity<ApiResponse<Void>> deleteBranch(
            @Parameter(description = "Branch ID") @PathVariable("id") String id) {
        branchService.deleteBranch(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Branch deleted successfully"));
    }
}
