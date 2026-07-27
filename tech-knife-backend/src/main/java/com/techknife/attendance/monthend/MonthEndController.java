package com.techknife.attendance.monthend;

import com.techknife.attendance.dto.MonthEndProcessDTO;
import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance/month-end")
@RequiredArgsConstructor
@Tag(name = "Month-End Attendance Process", description = "Endpoints for Summary Generation, Freezing, Payroll Export & Reprocessing")
@SecurityRequirement(name = "bearerAuth")
public class MonthEndController {

    private final MonthEndService monthEndService;

    @PostMapping("/process")
    @PreAuthorize("hasAuthority('ATTENDANCE_FREEZE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "PROCESS_MONTH_END_ATTENDANCE", module = "ATTENDANCE")
    @Operation(summary = "Generate Monthly Attendance Summary")
    public ResponseEntity<ApiResponse<MonthEndProcessDTO>> processMonthEnd(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false, defaultValue = "false") boolean forceReprocess,
            Principal principal) {
        String processedBy = principal != null ? principal.getName() : "HR_ADMIN";
        MonthEndProcessDTO result = monthEndService.processMonthEnd(year, month, processedBy, forceReprocess);
        return ResponseEntity.ok(ApiResponse.success(result, "Month-end attendance summary generated successfully"));
    }

    @PostMapping("/freeze")
    @PreAuthorize("hasAuthority('ATTENDANCE_FREEZE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "FREEZE_ATTENDANCE", module = "ATTENDANCE")
    @Operation(summary = "Freeze Monthly Attendance Records")
    public ResponseEntity<ApiResponse<MonthEndProcessDTO>> freezeAttendance(
            @RequestParam Integer year,
            @RequestParam Integer month,
            Principal principal) {
        String frozenBy = principal != null ? principal.getName() : "HR_ADMIN";
        MonthEndProcessDTO result = monthEndService.freezeAttendance(year, month, frozenBy);
        return ResponseEntity.ok(ApiResponse.success(result, "Attendance records frozen and locked successfully"));
    }

    @GetMapping("/payroll-ready")
    @PreAuthorize("hasAuthority('ATTENDANCE_FREEZE') or hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Payroll-Ready Attendance Summary")
    public ResponseEntity<ApiResponse<List<MonthEndProcessDTO.MonthlyAttendanceSummaryDTO>>> getPayrollReadySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        List<MonthEndProcessDTO.MonthlyAttendanceSummaryDTO> summary = monthEndService.getPayrollReadySummary(year, month);
        return ResponseEntity.ok(ApiResponse.success(summary, "Payroll-ready attendance summary retrieved successfully"));
    }
}
