package com.techknife.employee.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.employee.dto.OrgTreeNodeResponse;
import com.techknife.employee.dto.ReportingHierarchyResponse;
import com.techknife.employee.service.EmployeeHierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Hierarchy & Organization Tree", description = "Reporting lines, managers, subordinates and Org Chart")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeHierarchyController {

    private final EmployeeHierarchyService employeeHierarchyService;

    @GetMapping("/{id}/reporting-hierarchy")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Reporting Hierarchy", description = "Retrieves direct manager, skip-level manager, and subordinates")
    public ResponseEntity<ApiResponse<ReportingHierarchyResponse>> getReportingHierarchy(@PathVariable String id) {
        ReportingHierarchyResponse response = employeeHierarchyService.getReportingHierarchy(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporting hierarchy retrieved successfully"));
    }

    @GetMapping("/org-tree")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Organization Tree Chart", description = "Generates hierarchical organization tree chart")
    public ResponseEntity<ApiResponse<List<OrgTreeNodeResponse>>> getOrganizationTree(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String departmentId) {
        List<OrgTreeNodeResponse> response = employeeHierarchyService.getOrganizationTree(companyId, departmentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Organization tree generated successfully"));
    }
}
