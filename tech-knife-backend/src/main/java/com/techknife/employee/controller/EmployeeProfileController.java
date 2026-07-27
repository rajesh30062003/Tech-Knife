package com.techknife.employee.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.employee.dto.EmployeeResponse;
import com.techknife.employee.service.EmployeeProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Profile Lifecycle", description = "Activate, Deactivate, Suspend, Terminate, Resign, Retire endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeProfileController {

    private final EmployeeProfileService profileService;

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ACTIVATE_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Activate Employee Profile", description = "Sets employee status to ACTIVE")
    public ResponseEntity<ApiResponse<EmployeeResponse>> activateProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.activateProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile activated successfully"));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DEACTIVATE_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Deactivate Employee Profile", description = "Sets employee status to DEACTIVATED")
    public ResponseEntity<ApiResponse<EmployeeResponse>> deactivateProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.deactivateProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile deactivated successfully"));
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "SUSPEND_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Suspend Employee Profile", description = "Sets employee status to SUSPENDED")
    public ResponseEntity<ApiResponse<EmployeeResponse>> suspendProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.suspendProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile suspended successfully"));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "TERMINATE_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Terminate Employee Profile", description = "Sets employee status to TERMINATED")
    public ResponseEntity<ApiResponse<EmployeeResponse>> terminateProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.terminateProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile terminated successfully"));
    }

    @PostMapping("/{id}/resign")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "RESIGN_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Mark Employee Resigned", description = "Sets employee status to RESIGNED")
    public ResponseEntity<ApiResponse<EmployeeResponse>> resignProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.resignProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile updated to resigned successfully"));
    }

    @PostMapping("/{id}/retire")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "RETIRE_EMPLOYEE", resourceType = "EMPLOYEE")
    @Operation(summary = "Mark Employee Retired", description = "Sets employee status to RETIRED")
    public ResponseEntity<ApiResponse<EmployeeResponse>> retireProfile(
            @PathVariable String id,
            @RequestParam(required = false) String remarks,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "HR_ADMIN";
        EmployeeResponse response = profileService.retireProfile(id, remarks, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Employee profile updated to retired successfully"));
    }
}
