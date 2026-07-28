package com.techknife.backend.controller;

import com.techknife.backend.constant.Role;
import com.techknife.backend.dto.*;
import com.techknife.backend.security.UserPrincipal;
import com.techknife.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Enterprise Staff, Administrative Accounts and RBAC Assignment")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Retrieve paginated list of enterprise users with search and filter")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "department", required = false) String department) {
        PagedResponse<UserResponse> users = userService.getPaginatedUsers(page, size, search, department);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER') or #id == principal.id")
    @Operation(summary = "Get user account details by Mongo ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User details fetched"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Provision a new enterprise staff account")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "User account provisioned successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or #id == principal.id")
    @Operation(summary = "Update user account profile information")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("id") String id,
            @RequestBody UpdateUserRequest request) {
        UserResponse updated = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Deprovision user account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User account deprovisioned"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change authenticated user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password updated successfully"));
    }

    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Assign or revoke roles for specified user")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
            @PathVariable("id") String id,
            @RequestBody Set<Role> roles) {
        UserResponse updated = userService.assignRolesToUser(id, roles);
        return ResponseEntity.ok(ApiResponse.success(updated, "User roles updated"));
    }

    @PatchMapping("/{id}/lock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Toggle user account lock status")
    public ResponseEntity<ApiResponse<UserResponse>> toggleLock(
            @PathVariable("id") String id,
            @RequestParam("locked") boolean locked) {
        UserResponse updated = userService.toggleUserLock(id, locked);
        return ResponseEntity.ok(ApiResponse.success(updated, "User lock status updated"));
    }
}
