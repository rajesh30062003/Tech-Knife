package com.techknife.payroll.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.payroll.dto.SalaryStructureDTO;
import com.techknife.payroll.service.SalaryStructureService;
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
@RequestMapping("/api/v1/payroll/salary-structures")
@RequiredArgsConstructor
@Tag(name = "Payroll - Salary Structures", description = "Manage salary structures and grades")
@SecurityRequirement(name = "bearerAuth")
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @GetMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Salary Structures")
    public ResponseEntity<ApiResponse<List<SalaryStructureDTO>>> getAllStructures() {
        List<SalaryStructureDTO> result = salaryStructureService.getAllStructures();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched salary structures successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasAuthority('PAYROLL_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Salary Structure by ID")
    public ResponseEntity<ApiResponse<SalaryStructureDTO>> getStructureById(@PathVariable String id) {
        SalaryStructureDTO result = salaryStructureService.getStructureById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched salary structure successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PAYROLL, entityType = "SalaryStructure", description = "Created Salary Structure")
    @Operation(summary = "Create Salary Structure")
    public ResponseEntity<ApiResponse<SalaryStructureDTO>> createStructure(@Valid @RequestBody SalaryStructureDTO dto) {
        SalaryStructureDTO result = salaryStructureService.createStructure(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created salary structure successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PAYROLL, entityType = "SalaryStructure", description = "Updated Salary Structure")
    @Operation(summary = "Update Salary Structure")
    public ResponseEntity<ApiResponse<SalaryStructureDTO>> updateStructure(@PathVariable String id, @Valid @RequestBody SalaryStructureDTO dto) {
        SalaryStructureDTO result = salaryStructureService.updateStructure(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated salary structure successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SALARY_STRUCTURE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.PAYROLL, entityType = "SalaryStructure", description = "Deleted Salary Structure")
    @Operation(summary = "Delete Salary Structure")
    public ResponseEntity<ApiResponse<Void>> deleteStructure(@PathVariable String id) {
        salaryStructureService.deleteStructure(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted salary structure successfully"));
    }
}
