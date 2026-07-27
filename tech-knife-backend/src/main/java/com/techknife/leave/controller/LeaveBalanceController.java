package com.techknife.leave.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.leave.dto.LeaveBalanceDTO;
import com.techknife.leave.service.LeaveBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave/balances")
@RequiredArgsConstructor
@Tag(name = "Leave Balance Management", description = "Endpoints for employee leave balances, accruals, carry-forwards, and adjustments")
@SecurityRequirement(name = "bearerAuth")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Leave Balances", description = "Retrieves leave balances for an employee for a specified year")
    public ResponseEntity<ApiResponse<List<LeaveBalanceDTO>>> getEmployeeBalances(
            @PathVariable String employeeId,
            @RequestParam(required = false) Integer year) {
        int selectedYear = year != null ? year : Year.now().getValue();
        List<LeaveBalanceDTO> balances = leaveBalanceService.getEmployeeBalances(employeeId, selectedYear);
        return ResponseEntity.ok(ApiResponse.success(balances, "Employee leave balances retrieved successfully"));
    }

    @PostMapping("/employee/{employeeId}/initialize")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "INITIALIZE_LEAVE_BALANCES", module = "LEAVE")
    @Operation(summary = "Initialize Employee Balances")
    public ResponseEntity<ApiResponse<List<LeaveBalanceDTO>>> initializeBalances(
            @PathVariable String employeeId,
            @RequestParam(required = false) Integer year) {
        int selectedYear = year != null ? year : Year.now().getValue();
        List<LeaveBalanceDTO> balances = leaveBalanceService.initializeEmployeeBalances(employeeId, selectedYear);
        return ResponseEntity.ok(ApiResponse.success(balances, "Leave balances initialized successfully"));
    }

    @PostMapping("/employee/{employeeId}/adjust")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ADJUST_LEAVE_BALANCE", module = "LEAVE")
    @Operation(summary = "Adjust Employee Leave Balance")
    public ResponseEntity<ApiResponse<LeaveBalanceDTO>> adjustBalance(
            @PathVariable String employeeId,
            @RequestParam String leaveTypeId,
            @RequestParam Double additionalDays,
            @RequestParam String reason,
            @RequestParam(required = false) Integer year) {
        int selectedYear = year != null ? year : Year.now().getValue();
        LeaveBalanceDTO updated = leaveBalanceService.adjustBalance(employeeId, leaveTypeId, selectedYear, additionalDays, reason);
        return ResponseEntity.ok(ApiResponse.success(updated, "Leave balance adjusted successfully"));
    }

    @PostMapping("/employee/{employeeId}/carry-forward")
    @PreAuthorize("hasAuthority('LEAVE_POLICY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "PROCESS_CARRY_FORWARD", module = "LEAVE")
    @Operation(summary = "Process Carry Forward for Employee")
    public ResponseEntity<ApiResponse<Void>> processCarryForward(
            @PathVariable String employeeId,
            @RequestParam Integer fromYear,
            @RequestParam Integer toYear) {
        leaveBalanceService.processCarryForward(employeeId, fromYear, toYear);
        return ResponseEntity.ok(ApiResponse.success(null, "Carry forward processed successfully"));
    }
}
