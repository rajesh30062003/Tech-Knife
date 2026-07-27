package com.techknife.calendar;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.calendar.dto.CalendarEventDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Tag(name = "Unified Calendar", description = "Endpoints for Integrated Attendance, Leaves, Holidays, WFH & Events Calendar")
@SecurityRequirement(name = "bearerAuth")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW') or hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "VIEW_UNIFIED_CALENDAR", module = "CALENDAR")
    @Operation(summary = "Get Unified Calendar Events")
    public ResponseEntity<ApiResponse<List<CalendarEventDTO>>> getCalendarEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String employeeId) {
        List<CalendarEventDTO> events = calendarService.getUnifiedCalendarEvents(startDate, endDate, employeeId);
        return ResponseEntity.ok(ApiResponse.success(events, "Unified calendar events retrieved successfully"));
    }
}
