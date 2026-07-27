package com.techknife.leave.report;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.holiday.entity.Holiday;
import com.techknife.leave.dto.LeaveReportDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leaves/reports")
@RequiredArgsConstructor
@Tag(name = "Leave Reports", description = "Endpoints for Leave Register, Holiday & WFH Reports")
@SecurityRequirement(name = "bearerAuth")
public class LeaveReportController {

    private final LeaveReportService reportService;

    @GetMapping("/register")
    @PreAuthorize("hasAuthority('LEAVE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_LEAVE_REGISTER", module = "LEAVE")
    @Operation(summary = "Get Leave Register Report")
    public ResponseEntity<ApiResponse<List<LeaveReportDTO>>> getLeaveRegister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String departmentId) {
        List<LeaveReportDTO> reports = reportService.getLeaveRegister(startDate, endDate, departmentId);
        return ResponseEntity.ok(ApiResponse.success(reports, "Leave register report generated successfully"));
    }

    @GetMapping("/wfh")
    @PreAuthorize("hasAuthority('LEAVE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get WFH Report")
    public ResponseEntity<ApiResponse<List<LeaveReportDTO>>> getWfhReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String departmentId) {
        List<LeaveReportDTO> reports = reportService.getWfhReport(startDate, endDate, departmentId);
        return ResponseEntity.ok(ApiResponse.success(reports, "WFH report generated successfully"));
    }

    @GetMapping("/holidays")
    @PreAuthorize("hasAuthority('LEAVE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holiday Report")
    public ResponseEntity<ApiResponse<List<Holiday>>> getHolidayReport(@RequestParam(required = false) Integer year) {
        List<Holiday> holidays = reportService.getHolidayReport(year);
        return ResponseEntity.ok(ApiResponse.success(holidays, "Holiday report generated successfully"));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAuthority('LEAVE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "EXPORT_LEAVE_REPORT_CSV", module = "LEAVE")
    @Operation(summary = "Export Leave Report to CSV")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String departmentId) {
        List<LeaveReportDTO> reports = reportService.getLeaveRegister(startDate, endDate, departmentId);
        String csvData = reportService.exportToCsv(reports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=leave_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
