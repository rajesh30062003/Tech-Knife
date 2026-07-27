package com.techknife.backend.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.CreateEmployeeRequest;
import com.techknife.backend.dto.EmployeeResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.backend.dto.UpdateEmployeeRequest;
import com.techknife.backend.entity.Employee;
import com.techknife.backend.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Enterprise Staff Directory, Onboarding, Profile Updates, and Organizational Hierarchy")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Onboard a new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Employee created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Update employee record")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee details updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee profile by MongoDB ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable("id") String id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping("/code/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee profile by Employee Code ID")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmployeeId(@PathVariable("employeeId") String employeeId) {
        EmployeeResponse response = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping("/email/{officialEmail}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "Get employee profile by Official Email")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmail(@PathVariable("officialEmail") String officialEmail) {
        EmployeeResponse response = employeeService.getEmployeeByOfficialEmail(officialEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'HR', 'EMPLOYEE')")
    @Operation(summary = "List employees with search, department, manager, status filtering, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponse>>> getAllEmployees(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "managerId", required = false) String managerId,
            @RequestParam(value = "status", required = false) String status) {
        PagedResponse<EmployeeResponse> response = employeeService.getAllEmployees(page, size, search, departmentId, managerId, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Employees list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete an employee record")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable("id") String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Employee record deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR')")
    @Operation(summary = "Update employee employment status")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateStatus(
            @PathVariable("id") String id,
            @RequestParam("status") Employee.EmployeeStatus status) {
        EmployeeResponse response = employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee status updated successfully"));
    }
}
