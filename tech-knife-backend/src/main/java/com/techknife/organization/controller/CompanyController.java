package com.techknife.organization.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.CompanyRequest;
import com.techknife.organization.dto.CompanyResponse;
import com.techknife.organization.service.CompanyService;
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
@RequestMapping("/api/v1/organization/companies")
@RequiredArgsConstructor
@Auditable(module = "Company Management")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Company API", description = "Endpoints for managing Enterprise Legal Companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasAuthority('COMPANY_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Create new company", description = "Registers a new legal enterprise company record.")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Company created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Update company", description = "Updates details of an existing company.")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @Parameter(description = "Company ID") @PathVariable("id") String id,
            @Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Company updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get company by ID", description = "Retrieves company profile by unique ID.")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(
            @Parameter(description = "Company ID") @PathVariable("id") String id) {
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Company retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('COMPANY_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get company by Code", description = "Retrieves company profile by unique code.")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyByCode(
            @Parameter(description = "Company Code") @PathVariable("code") String code) {
        CompanyResponse response = companyService.getCompanyByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Company retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all companies", description = "Retrieves paginated list of registered companies.")
    public ResponseEntity<ApiResponse<PagedResponse<CompanyResponse>>> getAllCompanies(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search) {
        PagedResponse<CompanyResponse> response = companyService.getAllCompanies(page, size, search);
        return ResponseEntity.ok(ApiResponse.success(response, "Companies list retrieved successfully"));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('COMPANY_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get active companies", description = "Retrieves list of active companies.")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getActiveCompanies() {
        List<CompanyResponse> response = companyService.getAllActiveCompanies();
        return ResponseEntity.ok(ApiResponse.success(response, "Active companies list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPANY_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete company", description = "Removes a company record by ID.")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            @Parameter(description = "Company ID") @PathVariable("id") String id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Company deleted successfully"));
    }
}
