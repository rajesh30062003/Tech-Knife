package com.techknife.holiday.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.holiday.dto.RestrictedHolidayRequestDTO;
import com.techknife.holiday.service.RestrictedHolidayService;
import com.techknife.leave.entity.LeaveStatus;
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
@RequestMapping("/api/v1/holidays/restricted-requests")
@RequiredArgsConstructor
@Tag(name = "Restricted Holiday Requests", description = "Endpoints for opting for restricted/floating holidays")
@SecurityRequirement(name = "bearerAuth")
public class RestrictedHolidayController {

    private final RestrictedHolidayService restrictedHolidayService;

    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPLY_RESTRICTED_HOLIDAY", module = "HOLIDAY")
    @Operation(summary = "Apply for Restricted Holiday")
    public ResponseEntity<ApiResponse<RestrictedHolidayRequestDTO>> applyRestrictedHoliday(@Valid @RequestBody RestrictedHolidayRequestDTO dto) {
        RestrictedHolidayRequestDTO created = restrictedHolidayService.applyRestrictedHoliday(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Restricted holiday request submitted successfully"));
    }

    @PostMapping("/{id}/approve-reject")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPROVE_REJECT_RESTRICTED_HOLIDAY", module = "HOLIDAY")
    @Operation(summary = "Approve or Reject Restricted Holiday Request")
    public ResponseEntity<ApiResponse<RestrictedHolidayRequestDTO>> approveOrReject(
            @PathVariable String id,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String comments,
            Principal principal) {
        String approverId = principal != null ? principal.getName() : "APPROVER";
        RestrictedHolidayRequestDTO updated = restrictedHolidayService.approveOrRejectRestrictedHoliday(id, status, approverId, "Approver", comments);
        return ResponseEntity.ok(ApiResponse.success(updated, "Restricted holiday request status updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Restricted Holiday Request by ID")
    public ResponseEntity<ApiResponse<RestrictedHolidayRequestDTO>> getRequestById(@PathVariable String id) {
        RestrictedHolidayRequestDTO result = restrictedHolidayService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Restricted holiday request details retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Restricted Holiday Requests")
    public ResponseEntity<ApiResponse<List<RestrictedHolidayRequestDTO>>> getEmployeeRequests(
            @PathVariable String employeeId,
            @RequestParam(required = false) Integer year) {
        List<RestrictedHolidayRequestDTO> requests = restrictedHolidayService.getEmployeeRequests(employeeId, year);
        return ResponseEntity.ok(ApiResponse.success(requests, "Employee restricted holiday requests retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending Restricted Holiday Requests for Approver")
    public ResponseEntity<ApiResponse<List<RestrictedHolidayRequestDTO>>> getPendingRequests(Principal principal) {
        String approverId = principal != null ? principal.getName() : "HR_ADMIN";
        List<RestrictedHolidayRequestDTO> requests = restrictedHolidayService.getPendingRequestsForApprover(approverId);
        return ResponseEntity.ok(ApiResponse.success(requests, "Pending restricted holiday requests retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Restricted Holiday Requests")
    public ResponseEntity<ApiResponse<List<RestrictedHolidayRequestDTO>>> getAllRequests() {
        List<RestrictedHolidayRequestDTO> requests = restrictedHolidayService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.success(requests, "All restricted holiday requests retrieved successfully"));
    }
}
