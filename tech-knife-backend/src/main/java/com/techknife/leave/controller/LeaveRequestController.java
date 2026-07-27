package com.techknife.leave.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeaveApprovalDTO;
import com.techknife.leave.dto.LeaveRequestCreateDTO;
import com.techknife.leave.dto.LeaveRequestDTO;
import com.techknife.leave.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave/requests")
@RequiredArgsConstructor
@Tag(name = "Leave Request & Approvals", description = "Endpoints for applying, approving, rejecting, and canceling leave applications")
@SecurityRequirement(name = "bearerAuth")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPLY_LEAVE", module = "LEAVE")
    @Operation(summary = "Apply for Leave", description = "Submits a new leave request and checks quota/overlap")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> applyLeave(@Valid @RequestBody LeaveRequestCreateDTO dto) {
        LeaveRequestDTO created = leaveRequestService.applyLeave(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Leave request submitted successfully"));
    }

    @PostMapping("/{id}/approve-reject")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPROVE_REJECT_LEAVE", module = "LEAVE")
    @Operation(summary = "Approve or Reject Leave", description = "Managers/HR process pending leave request")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> approveOrRejectLeave(
            @PathVariable String id,
            @Valid @RequestBody LeaveApprovalDTO approvalDTO,
            Principal principal) {
        String approverId = principal != null ? principal.getName() : "CURRENT_USER";
        LeaveRequestDTO updated = leaveRequestService.approveOrRejectLeave(id, approvalDTO, approverId, "Approver", "MANAGER");
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave request status updated successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('LEAVE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CANCEL_LEAVE", module = "LEAVE")
    @Operation(summary = "Cancel Leave Request", description = "Employees cancel their pending/approved leave")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> cancelLeave(
            @PathVariable String id,
            @RequestParam String employeeId,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        LeaveRequestDTO updated = leaveRequestService.cancelLeave(id, employeeId, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave request cancelled successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Leave Request Details")
    public ResponseEntity<ApiResponse<LeaveRequestDTO>> getLeaveRequestById(@PathVariable String id) {
        LeaveRequestDTO result = leaveRequestService.getLeaveRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Leave request details retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Leave Requests")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getEmployeeLeaveRequests(@PathVariable String employeeId) {
        List<LeaveRequestDTO> requests = leaveRequestService.getEmployeeLeaveRequests(employeeId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Employee leave requests retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending Approvals for Approver")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getPendingApprovals(Principal principal) {
        String approverId = principal != null ? principal.getName() : "HR_ADMIN";
        List<LeaveRequestDTO> requests = leaveRequestService.getPendingApprovalsForUser(approverId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Pending leave approvals retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Leave Requests")
    public ResponseEntity<ApiResponse<List<LeaveRequestDTO>>> getAllLeaveRequests() {
        List<LeaveRequestDTO> requests = leaveRequestService.getAllLeaveRequests();
        return ResponseEntity.ok(ApiResponse.success(requests, "All leave requests retrieved successfully"));
    }
}
