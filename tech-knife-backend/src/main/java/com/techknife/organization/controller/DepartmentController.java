package com.techknife.organization.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.DepartmentRequest;
import com.techknife.organization.dto.DepartmentResponse;
import com.techknife.organization.service.DepartmentService;
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
@RequestMapping("/api/v1/organization/departments")
@RequiredArgsConstructor
@Auditable(module = "Department Management")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Department API", description = "Endpoints for managing Organizational Departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Create department", description = "Registers a new department.")
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Department created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Update department", description = "Updates details of an existing department.")
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @Parameter(description = "Department ID") @PathVariable("id") String id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Department updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get department by ID", description = "Retrieves department details by ID.")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @Parameter(description = "Department ID") @PathVariable("id") String id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Department retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get department by Code", description = "Retrieves department details by code.")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentByCode(
            @Parameter(description = "Department Code") @PathVariable("code") String code) {
        DepartmentResponse response = departmentService.getDepartmentByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Department retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all departments", description = "Retrieves paginated list of departments.")
    public ResponseEntity<ApiResponse<PagedResponse<DepartmentResponse>>> getAllDepartments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "companyId", required = false) String companyId) {
        PagedResponse<DepartmentResponse> response = departmentService.getAllDepartments(page, size, companyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Departments list retrieved successfully"));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get departments by company", description = "Lists departments by company ID.")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsByCompany(
            @Parameter(description = "Company ID") @PathVariable("companyId") String companyId) {
        List<DepartmentResponse> response = departmentService.getDepartmentsByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success(response, "Departments list retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAuthority('DEPARTMENT_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get departments by branch", description = "Lists departments by branch ID.")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getDepartmentsByBranch(
            @Parameter(description = "Branch ID") @PathVariable("branchId") String branchId) {
        List<DepartmentResponse> response = departmentService.getDepartmentsByBranch(branchId);
        return ResponseEntity.ok(ApiResponse.success(response, "Departments list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete department", description = "Removes a department record.")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @Parameter(description = "Department ID") @PathVariable("id") String id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Department deleted successfully"));
    }
}
