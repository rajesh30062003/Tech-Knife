package com.techknife.attendance.report;

import com.techknife.attendance.dto.AttendanceReportDTO;
import com.techknife.attendance.dto.CompOffDTO;
import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
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
@RequestMapping("/api/v1/attendance/reports")
@RequiredArgsConstructor
@Tag(name = "Attendance Reports", description = "Endpoints for Attendance Register, Daily, Weekly, Monthly, Late & Overtime Reports")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceReportController {

    private final AttendanceReportService reportService;

    @GetMapping("/register")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_ATTENDANCE_REGISTER", module = "ATTENDANCE")
    @Operation(summary = "Get Attendance Register Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getRegister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String departmentId) {
        List<AttendanceReportDTO> reports = reportService.getAttendanceRegister(startDate, endDate, departmentId);
        return ResponseEntity.ok(ApiResponse.success(reports, "Attendance register report generated successfully"));
    }

    @GetMapping("/daily")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Daily Attendance Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceReportDTO> reports = reportService.getDailyReport(date);
        return ResponseEntity.ok(ApiResponse.success(reports, "Daily attendance report generated successfully"));
    }

    @GetMapping("/weekly")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Weekly Attendance Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getWeeklyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        List<AttendanceReportDTO> reports = reportService.getWeeklyReport(startDate);
        return ResponseEntity.ok(ApiResponse.success(reports, "Weekly attendance report generated successfully"));
    }

    @GetMapping("/monthly")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Monthly Attendance Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getMonthlyReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        List<AttendanceReportDTO> reports = reportService.getMonthlyReport(year, month);
        return ResponseEntity.ok(ApiResponse.success(reports, "Monthly attendance report generated successfully"));
    }

    @GetMapping("/shift")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Shift Attendance Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getShiftReport(
            @RequestParam(required = false) String shiftId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceReportDTO> reports = reportService.getShiftReport(shiftId, date);
        return ResponseEntity.ok(ApiResponse.success(reports, "Shift attendance report generated successfully"));
    }

    @GetMapping("/late-arrival")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Late Arrival Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getLateArrivalReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceReportDTO> reports = reportService.getLateArrivalReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(reports, "Late arrival report generated successfully"));
    }

    @GetMapping("/overtime")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Overtime Report")
    public ResponseEntity<ApiResponse<List<AttendanceReportDTO>>> getOvertimeReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<AttendanceReportDTO> reports = reportService.getOvertimeReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(reports, "Overtime report generated successfully"));
    }

    @GetMapping("/comp-off")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Comp-Off Report")
    public ResponseEntity<ApiResponse<List<CompOffDTO>>> getCompOffReport() {
        List<CompOffDTO> reports = reportService.getCompOffReport();
        return ResponseEntity.ok(ApiResponse.success(reports, "Comp-off report generated successfully"));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAuthority('ATTENDANCE_REPORT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "EXPORT_ATTENDANCE_REPORT_CSV", module = "ATTENDANCE")
    @Operation(summary = "Export Attendance Report to CSV")
    public ResponseEntity<String> exportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String departmentId) {
        List<AttendanceReportDTO> reports = reportService.getAttendanceRegister(startDate, endDate, departmentId);
        String csvData = reportService.exportToCsv(reports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
