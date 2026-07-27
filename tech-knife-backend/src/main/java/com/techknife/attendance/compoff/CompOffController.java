package com.techknife.attendance.compoff;

import com.techknife.attendance.dto.CompOffDTO;
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
@RequestMapping("/api/v1/attendance/comp-off")
@RequiredArgsConstructor
@Tag(name = "Comp-Off Management", description = "Endpoints for Generating, Approving, Consuming & Balance Tracking Comp-Offs")
@SecurityRequirement(name = "bearerAuth")
public class CompOffController {

    private final CompOffService compOffService;

    @PostMapping
    @PreAuthorize("hasAuthority('COMPOFF_MANAGE') or hasAuthority('ATTENDANCE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "GENERATE_COMP_OFF", module = "ATTENDANCE")
    @Operation(summary = "Generate/Request Comp-Off")
    public ResponseEntity<ApiResponse<CompOffDTO>> generateCompOff(@Valid @RequestBody CompOffDTO.Request request) {
        CompOffDTO created = compOffService.generateCompOff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Comp-Off request submitted successfully"));
    }

    @PostMapping("/{id}/approve-reject")
    @PreAuthorize("hasAuthority('COMPOFF_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "APPROVE_REJECT_COMP_OFF", module = "ATTENDANCE")
    @Operation(summary = "Approve or Reject Comp-Off")
    public ResponseEntity<ApiResponse<CompOffDTO>> approveOrReject(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String comments,
            Principal principal) {
        String approverId = principal != null ? principal.getName() : "APPROVER";
        CompOffDTO updated = compOffService.approveOrRejectCompOff(id, status, approverId, comments);
        return ResponseEntity.ok(ApiResponse.success(updated, "Comp-Off status updated successfully"));
    }

    @PostMapping("/consume")
    @PreAuthorize("hasAuthority('ATTENDANCE_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CONSUME_COMP_OFF", module = "ATTENDANCE")
    @Operation(summary = "Consume Comp-Off")
    public ResponseEntity<ApiResponse<CompOffDTO>> consumeCompOff(
            @RequestParam String employeeId,
            @RequestParam Double days,
            @RequestParam String reason) {
        CompOffDTO consumed = compOffService.consumeCompOff(employeeId, days, reason);
        return ResponseEntity.ok(ApiResponse.success(consumed, "Comp-Off consumed successfully"));
    }

    @GetMapping("/balance/{employeeId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Comp-Off Balance")
    public ResponseEntity<ApiResponse<CompOffDTO.Balance>> getBalance(@PathVariable String employeeId) {
        CompOffDTO.Balance balance = compOffService.getBalance(employeeId);
        return ResponseEntity.ok(ApiResponse.success(balance, "Comp-Off balance retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Comp-Off History")
    public ResponseEntity<ApiResponse<List<CompOffDTO>>> getEmployeeCompOffs(@PathVariable String employeeId) {
        List<CompOffDTO> history = compOffService.getEmployeeCompOffs(employeeId);
        return ResponseEntity.ok(ApiResponse.success(history, "Employee Comp-Off history retrieved successfully"));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('COMPOFF_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Pending Comp-Off Requests")
    public ResponseEntity<ApiResponse<List<CompOffDTO>>> getPendingCompOffs() {
        List<CompOffDTO> pending = compOffService.getPendingCompOffs();
        return ResponseEntity.ok(ApiResponse.success(pending, "Pending Comp-Off requests retrieved successfully"));
    }
}
