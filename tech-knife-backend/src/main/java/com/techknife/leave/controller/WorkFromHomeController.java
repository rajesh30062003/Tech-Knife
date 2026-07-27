package com.techknife.leave.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.WFHRequestCreateDTO;
import com.techknife.leave.dto.WFHRequestDTO;
import com.techknife.leave.entity.WFHStatus;
import com.techknife.leave.service.WorkFromHomeService;
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
@RequestMapping("/api/v1/leave/wfh")
@RequiredArgsConstructor
@Tag(name = "Work From Home Management", description = "Endpoints for requesting, approving, and tracking WFH")
@SecurityRequirement(name = "bearerAuth")
public class WorkFromHomeController {

    private final WorkFromHomeService wfhService;

    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPLY_WFH", module = "LEAVE")
    @Operation(summary = "Apply for Work From Home")
    public ResponseEntity<ApiResponse<WFHRequestDTO>> applyWFH(@Valid @RequestBody WFHRequestCreateDTO dto) {
        WFHRequestDTO created = wfhService.applyWFH(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "WFH request submitted successfully"));
    }

    @PostMapping("/{id}/approve-reject")
    @PreAuthorize("hasAuthority('WFH_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPROVE_REJECT_WFH", module = "LEAVE")
    @Operation(summary = "Approve or Reject WFH Request")
    public ResponseEntity<ApiResponse<WFHRequestDTO>> approveOrRejectWFH(
            @PathVariable String id,
            @RequestParam WFHStatus status,
            @RequestParam(required = false) String comments,
            Principal principal) {
        String approverId = principal != null ? principal.getName() : "APPROVER";
        WFHRequestDTO updated = wfhService.approveOrRejectWFH(id, status, approverId, "Approver", comments);
        return ResponseEntity.ok(ApiResponse.success(updated, "WFH request status updated successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('LEAVE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CANCEL_WFH", module = "LEAVE")
    @Operation(summary = "Cancel WFH Request")
    public ResponseEntity<ApiResponse<WFHRequestDTO>> cancelWFH(
            @PathVariable String id,
            @RequestParam String employeeId) {
        WFHRequestDTO updated = wfhService.cancelWFH(id, employeeId);
        return ResponseEntity.ok(ApiResponse.success(updated, "WFH request cancelled successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get WFH Request by ID")
    public ResponseEntity<ApiResponse<WFHRequestDTO>> getWFHById(@PathVariable String id) {
        WFHRequestDTO result = wfhService.getWFHById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "WFH request details retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee WFH Requests")
    public ResponseEntity<ApiResponse<List<WFHRequestDTO>>> getEmployeeWFHRequests(@PathVariable String employeeId) {
        List<WFHRequestDTO> requests = wfhService.getEmployeeWFHRequests(employeeId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Employee WFH requests retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('WFH_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending WFH Requests for Approver")
    public ResponseEntity<ApiResponse<List<WFHRequestDTO>>> getPendingWFHRequests(Principal principal) {
        String approverId = principal != null ? principal.getName() : "HR_ADMIN";
        List<WFHRequestDTO> requests = wfhService.getPendingWFHForApprover(approverId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Pending WFH requests retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All WFH Requests")
    public ResponseEntity<ApiResponse<List<WFHRequestDTO>>> getAllWFHRequests() {
        List<WFHRequestDTO> requests = wfhService.getAllWFHRequests();
        return ResponseEntity.ok(ApiResponse.success(requests, "All WFH requests retrieved successfully"));
    }
}
