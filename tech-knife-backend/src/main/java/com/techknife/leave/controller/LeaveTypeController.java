package com.techknife.leave.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeaveTypeDTO;
import com.techknife.leave.service.LeaveTypeService;
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
@RequestMapping("/api/v1/leave/types")
@RequiredArgsConstructor
@Tag(name = "Leave Type Management", description = "Endpoints for configuring leave categories (Casual, Sick, Earned, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_LEAVE_TYPE", module = "LEAVE")
    @Operation(summary = "Create Leave Type", description = "Defines a new leave type configuration")
    public ResponseEntity<ApiResponse<LeaveTypeDTO>> createLeaveType(@Valid @RequestBody LeaveTypeDTO dto) {
        LeaveTypeDTO created = leaveTypeService.createLeaveType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Leave type created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_LEAVE_TYPE", module = "LEAVE")
    @Operation(summary = "Update Leave Type", description = "Updates existing leave type configuration")
    public ResponseEntity<ApiResponse<LeaveTypeDTO>> updateLeaveType(@PathVariable String id, @Valid @RequestBody LeaveTypeDTO dto) {
        LeaveTypeDTO updated = leaveTypeService.updateLeaveType(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave type updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Leave Type by ID")
    public ResponseEntity<ApiResponse<LeaveTypeDTO>> getLeaveTypeById(@PathVariable String id) {
        LeaveTypeDTO result = leaveTypeService.getLeaveTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Leave type retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Leave Types")
    public ResponseEntity<ApiResponse<List<LeaveTypeDTO>>> getAllLeaveTypes(@RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        List<LeaveTypeDTO> result = activeOnly ? leaveTypeService.getAllActiveLeaveTypes() : leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(ApiResponse.success(result, "Leave types retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_LEAVE_TYPE", module = "LEAVE")
    @Operation(summary = "Delete Leave Type")
    public ResponseEntity<ApiResponse<Void>> deleteLeaveType(@PathVariable String id) {
        leaveTypeService.deleteLeaveType(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Leave type deleted successfully"));
    }
}
