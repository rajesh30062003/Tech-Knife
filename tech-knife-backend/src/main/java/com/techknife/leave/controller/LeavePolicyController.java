package com.techknife.leave.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeavePolicyDTO;
import com.techknife.leave.service.LeavePolicyService;
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
@RequestMapping("/api/v1/leave/policies")
@RequiredArgsConstructor
@Tag(name = "Leave Policy Management", description = "Department/Designation/Branch rules for leaves")
@SecurityRequirement(name = "bearerAuth")
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;

    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_LEAVE_POLICY", module = "LEAVE")
    @Operation(summary = "Create Leave Policy")
    public ResponseEntity<ApiResponse<LeavePolicyDTO>> createPolicy(@Valid @RequestBody LeavePolicyDTO dto) {
        LeavePolicyDTO created = leavePolicyService.createPolicy(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Leave policy created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_LEAVE_POLICY", module = "LEAVE")
    @Operation(summary = "Update Leave Policy")
    public ResponseEntity<ApiResponse<LeavePolicyDTO>> updatePolicy(@PathVariable String id, @Valid @RequestBody LeavePolicyDTO dto) {
        LeavePolicyDTO updated = leavePolicyService.updatePolicy(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave policy updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Leave Policy by ID")
    public ResponseEntity<ApiResponse<LeavePolicyDTO>> getPolicyById(@PathVariable String id) {
        LeavePolicyDTO result = leavePolicyService.getPolicyById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Leave policy retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Leave Policies")
    public ResponseEntity<ApiResponse<List<LeavePolicyDTO>>> getAllPolicies() {
        List<LeavePolicyDTO> result = leavePolicyService.getAllPolicies();
        return ResponseEntity.ok(ApiResponse.success(result, "Leave policies retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_LEAVE_POLICY", module = "LEAVE")
    @Operation(summary = "Delete Leave Policy")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable String id) {
        leavePolicyService.deletePolicy(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Leave policy deleted successfully"));
    }
}
