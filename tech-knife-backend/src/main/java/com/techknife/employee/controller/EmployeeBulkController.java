package com.techknife.employee.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.employee.dto.BulkDepartmentTransferRequest;
import com.techknife.employee.dto.BulkOperationResponse;
import com.techknife.employee.dto.BulkStatusChangeRequest;
import com.techknife.employee.entity.EmployeeStatus;
import com.techknife.employee.service.EmployeeBulkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/employees/bulk")
@RequiredArgsConstructor
@Tag(name = "Employee Bulk Operations & Import/Export", description = "CSV Bulk Import, Export, Status Change, Department Transfer")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeBulkController {

    private final EmployeeBulkService bulkService;

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "BULK_IMPORT_EMPLOYEES", resourceType = "EMPLOYEE")
    @Operation(summary = "Bulk Import Employees from CSV", description = "Processes CSV file, validates duplicates, and imports employees")
    public ResponseEntity<ApiResponse<BulkOperationResponse>> bulkImport(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        BulkOperationResponse response = bulkService.bulkImport(file, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk import completed"));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Bulk Export Employees as CSV", description = "Downloads filtered employee records in CSV format")
    public ResponseEntity<byte[]> bulkExport(
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) EmployeeStatus status) {
        byte[] csvData = bulkService.bulkExportCSV(departmentId, status);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employees_export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @PostMapping("/status-change")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "BULK_STATUS_CHANGE", resourceType = "EMPLOYEE")
    @Operation(summary = "Bulk Status Change", description = "Updates status for multiple employees at once")
    public ResponseEntity<ApiResponse<BulkOperationResponse>> bulkStatusChange(
            @Valid @RequestBody BulkStatusChangeRequest request,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        BulkOperationResponse response = bulkService.bulkStatusChange(request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk status change completed"));
    }

    @PostMapping("/department-transfer")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "BULK_DEPARTMENT_TRANSFER", resourceType = "EMPLOYEE")
    @Operation(summary = "Bulk Department Transfer", description = "Transfers multiple employees to a target department")
    public ResponseEntity<ApiResponse<BulkOperationResponse>> bulkDepartmentTransfer(
            @Valid @RequestBody BulkDepartmentTransferRequest request,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        BulkOperationResponse response = bulkService.bulkDepartmentTransfer(request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Bulk department transfer completed"));
    }
}
