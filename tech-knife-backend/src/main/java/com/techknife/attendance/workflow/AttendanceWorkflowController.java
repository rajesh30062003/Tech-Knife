package com.techknife.attendance.workflow;

import com.techknife.attendance.dto.AttendanceRegularizationDTO;
import com.techknife.attendance.entity.RegularizationStatus;
import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
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
@RequestMapping("/api/v1/attendance/regularization")
@RequiredArgsConstructor
@Tag(name = "Attendance Regularization Workflow", description = "Endpoints for Missed Check-In/Out, Late Justifications & Approvals")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceWorkflowController {

    private final AttendanceWorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPLY_ATTENDANCE_REGULARIZATION", module = "ATTENDANCE")
    @Operation(summary = "Submit Attendance Regularization Request")
    public ResponseEntity<ApiResponse<AttendanceRegularizationDTO>> submitRegularization(
            @Valid @RequestBody AttendanceRegularizationDTO.Request request) {
        AttendanceRegularizationDTO created = workflowService.submitRegularization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Regularization request submitted successfully"));
    }

    @PostMapping("/{id}/approve-reject")
    @PreAuthorize("hasAuthority('ATTENDANCE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPROVE_REJECT_ATTENDANCE_REGULARIZATION", module = "ATTENDANCE")
    @Operation(summary = "Approve or Reject Attendance Regularization")
    public ResponseEntity<ApiResponse<AttendanceRegularizationDTO>> approveOrReject(
            @PathVariable String id,
            @RequestParam RegularizationStatus status,
            @RequestParam(required = false) String comments,
            Principal principal) {
        String approverId = principal != null ? principal.getName() : "APPROVER";
        AttendanceRegularizationDTO updated = workflowService.approveOrReject(id, status, approverId, "Approver", comments);
        return ResponseEntity.ok(ApiResponse.success(updated, "Regularization request status updated successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Attendance Regularization History for Employee")
    public ResponseEntity<ApiResponse<List<AttendanceRegularizationDTO>>> getEmployeeHistory(@PathVariable String employeeId) {
        List<AttendanceRegularizationDTO> history = workflowService.getEmployeeHistory(employeeId);
        return ResponseEntity.ok(ApiResponse.success(history, "Employee regularization history retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ATTENDANCE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending Regularization Requests for Approver")
    public ResponseEntity<ApiResponse<List<AttendanceRegularizationDTO>>> getPendingRequests(Principal principal) {
        String approverId = principal != null ? principal.getName() : "MANAGER";
        List<AttendanceRegularizationDTO> pending = workflowService.getPendingForApprover(approverId);
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending regularization requests retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Attendance Regularization Requests")
    public ResponseEntity<ApiResponse<List<AttendanceRegularizationDTO>>> getAllRegularizations() {
        List<AttendanceRegularizationDTO> all = workflowService.getAllRegularizations();
        return ResponseEntity.ok(ApiResponse.success(all, "All regularization requests retrieved successfully"));
    }
}
