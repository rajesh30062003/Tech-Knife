package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.EmployeeSalaryDTO;
import com.techknife.payroll.service.EmployeeSalaryService;
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
@RequestMapping("/api/v1/payroll/employee-salaries")
@RequiredArgsConstructor
@Tag(name = "Payroll - Employee Salaries", description = "Manage individual employee salary details")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeSalaryController {

    private final EmployeeSalaryService employeeSalaryService;

    @GetMapping
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Employee Salaries")
    public ResponseEntity<ApiResponse<List<EmployeeSalaryDTO>>> getAllEmployeeSalaries() {
        List<EmployeeSalaryDTO> result = employeeSalaryService.getAllEmployeeSalaries();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee salaries successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Salaries by Employee ID")
    public ResponseEntity<ApiResponse<List<EmployeeSalaryDTO>>> getSalariesByEmployeeId(@PathVariable String employeeId) {
        List<EmployeeSalaryDTO> result = employeeSalaryService.getSalariesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee salary details successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYROLL_VIEW') or hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Salary by Record ID")
    public ResponseEntity<ApiResponse<EmployeeSalaryDTO>> getEmployeeSalaryById(@PathVariable String id) {
        EmployeeSalaryDTO result = employeeSalaryService.getEmployeeSalaryById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched employee salary details successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "EmployeeSalary", description = "Created Employee Salary")
    @Operation(summary = "Create Employee Salary")
    public ResponseEntity<ApiResponse<EmployeeSalaryDTO>> createEmployeeSalary(@Valid @RequestBody EmployeeSalaryDTO dto) {
        EmployeeSalaryDTO result = employeeSalaryService.createEmployeeSalary(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created employee salary successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_PROCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "EmployeeSalary", description = "Updated Employee Salary")
    @Operation(summary = "Update Employee Salary")
    public ResponseEntity<ApiResponse<EmployeeSalaryDTO>> updateEmployeeSalary(@PathVariable String id, @Valid @RequestBody EmployeeSalaryDTO dto) {
        EmployeeSalaryDTO result = employeeSalaryService.updateEmployeeSalary(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated employee salary successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "EmployeeSalary", description = "Deleted Employee Salary")
    @Operation(summary = "Delete Employee Salary")
    public ResponseEntity<ApiResponse<Void>> deleteEmployeeSalary(@PathVariable String id) {
        employeeSalaryService.deleteEmployeeSalary(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted employee salary record successfully"));
    }
}
