package com.techknife.backend.controller;

import com.techknife.backend.dto.*;
import com.techknife.backend.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "Enterprise daily check-in, check-out, break tracking, WFH, admin correction, and monthly analytics")
public class AttendanceController {

    private final AttendanceService attendanceService;

    // --- Employee Endpoints ---

    @PostMapping("/check-in")
    @Operation(summary = "Record daily Check-In punch with location and WFH status")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> checkIn(@Valid @RequestBody CheckInRequestDto dto) {
        AttendanceResponseDto response = attendanceService.checkIn(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Check-in recorded successfully"));
    }

    @PostMapping("/{id}/check-out")
    @Operation(summary = "Record daily Check-Out punch and calculate working & overtime hours")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> checkOut(
            @PathVariable("id") String id,
            @RequestBody CheckOutRequestDto dto) {
        AttendanceResponseDto response = attendanceService.checkOut(id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Check-out recorded successfully"));
    }

    @PostMapping("/{id}/break")
    @Operation(summary = "Toggle Break Start or Break End punch")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> toggleBreak(
            @PathVariable("id") String id,
            @Valid @RequestBody BreakRequestDto dto) {
        AttendanceResponseDto response = attendanceService.toggleBreak(id, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Break status updated successfully"));
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's real-time attendance record for employee")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> getTodayAttendance(@RequestParam("userId") String userId) {
        AttendanceResponseDto response = attendanceService.getTodayAttendance(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Today's attendance record fetched"));
    }

    @GetMapping("/history")
    @Operation(summary = "Get employee personal attendance history filtered by date range")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getHistory(
            @RequestParam("userId") String userId,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<AttendanceResponseDto> history = attendanceService.getUserAttendanceHistory(userId, fromDate, toDate);
        return ResponseEntity.ok(ApiResponse.success(history, "Attendance history retrieved"));
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get monthly attendance calendar view for employee")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> getCalendar(
            @RequestParam("userId") String userId,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        List<AttendanceResponseDto> calendar = attendanceService.getUserMonthlyCalendar(userId, year, month);
        return ResponseEntity.ok(ApiResponse.success(calendar, "Monthly calendar data fetched"));
    }

    // --- Admin & HR Management Endpoints ---

    @PutMapping("/{id}/correct")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Correct punch log or attendance entry with mandatory administrative reason")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> correctAttendance(
            @PathVariable("id") String id,
            @Valid @RequestBody AttendanceCorrectionRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "admin@techknife.io";
        AttendanceResponseDto response = attendanceService.correctAttendance(id, dto, adminEmail);
        return ResponseEntity.ok(ApiResponse.success(response, "Attendance record corrected by admin"));
    }

    @PostMapping("/manual")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Create manual attendance entry for employee")
    public ResponseEntity<ApiResponse<AttendanceResponseDto>> createManualAttendance(
            @Valid @RequestBody ManualAttendanceRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "admin@techknife.io";
        AttendanceResponseDto response = attendanceService.createManualAttendance(dto, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Manual attendance created"));
    }

    @PostMapping("/bulk-import")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Bulk import attendance records")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDto>>> bulkImportAttendance(
            @Valid @RequestBody BulkAttendanceImportDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String adminEmail = userDetails != null ? userDetails.getUsername() : "admin@techknife.io";
        List<AttendanceResponseDto> response = attendanceService.bulkImportAttendance(dto, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Bulk attendance import completed successfully"));
    }

    @GetMapping("/monthly-summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'DIRECTOR')")
    @Operation(summary = "Get monthly aggregated attendance summary and metrics by department")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryDto>>> getMonthlySummary(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam(value = "department", required = false) String department) {
        List<AttendanceSummaryDto> summaries = attendanceService.getMonthlyAttendanceSummary(year, month, department);
        return ResponseEntity.ok(ApiResponse.success(summaries, "Monthly attendance summary generated"));
    }

    @GetMapping("/yearly-summary")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Get yearly attendance summary and overtime breakdown for employee")
    public ResponseEntity<ApiResponse<AttendanceSummaryDto>> getYearlySummary(
            @RequestParam("year") int year,
            @RequestParam("userId") String userId) {
        AttendanceSummaryDto summary = attendanceService.getYearlyAttendanceSummary(year, userId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Yearly attendance summary generated"));
    }
}
