package com.techknife.backend.controller;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.RoleRequest;
import com.techknife.backend.dto.RoleResponse;
import com.techknife.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Role Governance", description = "Role Definitions and Permission Mapping Management")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Retrieve all registered system and custom roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success(roles, "System roles retrieved successfully"));
    }

    @GetMapping("/enum/{role}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Retrieve role details by role enum key")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByEnum(@PathVariable("role") Role role) {
        RoleResponse roleResponse = roleService.getRoleByEnum(role);
        return ResponseEntity.ok(ApiResponse.success(roleResponse, "Role details retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Retrieve role by unique Mongo identifier")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable("id") String id) {
        RoleResponse roleResponse = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(roleResponse, "Role fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Create a custom system role definition")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest.CreateRoleRequest request) {
        RoleResponse roleResponse = roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(roleResponse, "Custom role created successfully"));
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Update permission mappings assigned to a role")
    public ResponseEntity<ApiResponse<RoleResponse>> updatePermissions(
            @PathVariable("id") String id,
            @Valid @RequestBody RoleRequest.UpdateRolePermissionsRequest request) {
        RoleResponse updated = roleService.updateRolePermissions(id, request.getPermissions());
        return ResponseEntity.ok(ApiResponse.success(updated, "Role permissions updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Delete a custom non-system role")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable("id") String id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Role deleted successfully"));
    }
}
