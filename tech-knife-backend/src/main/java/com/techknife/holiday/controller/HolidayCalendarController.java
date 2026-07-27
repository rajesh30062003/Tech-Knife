package com.techknife.holiday.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.holiday.dto.HolidayCalendarDTO;
import com.techknife.holiday.service.HolidayCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holidays/calendars")
@RequiredArgsConstructor
@Tag(name = "Holiday Calendar Management", description = "Endpoints for branch/year holiday calendar configurations")
@SecurityRequirement(name = "bearerAuth")
public class HolidayCalendarController {

    private final HolidayCalendarService calendarService;

    @PostMapping
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_HOLIDAY_CALENDAR", module = "HOLIDAY")
    @Operation(summary = "Create Holiday Calendar")
    public ResponseEntity<ApiResponse<HolidayCalendarDTO>> createCalendar(@Valid @RequestBody HolidayCalendarDTO dto) {
        HolidayCalendarDTO created = calendarService.createCalendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Holiday calendar created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPDATE_HOLIDAY_CALENDAR", module = "HOLIDAY")
    @Operation(summary = "Update Holiday Calendar")
    public ResponseEntity<ApiResponse<HolidayCalendarDTO>> updateCalendar(@PathVariable String id, @Valid @RequestBody HolidayCalendarDTO dto) {
        HolidayCalendarDTO updated = calendarService.updateCalendar(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Holiday calendar updated successfully"));
    }

    @PostMapping("/{id}/holidays/{holidayId}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "ADD_HOLIDAY_TO_CALENDAR", module = "HOLIDAY")
    @Operation(summary = "Add Holiday to Calendar")
    public ResponseEntity<ApiResponse<HolidayCalendarDTO>> addHolidayToCalendar(@PathVariable String id, @PathVariable String holidayId) {
        HolidayCalendarDTO updated = calendarService.addHolidayToCalendar(id, holidayId);
        return ResponseEntity.ok(ApiResponse.success(updated, "Holiday added to calendar successfully"));
    }

    @DeleteMapping("/{id}/holidays/{holidayId}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "REMOVE_HOLIDAY_FROM_CALENDAR", module = "HOLIDAY")
    @Operation(summary = "Remove Holiday from Calendar")
    public ResponseEntity<ApiResponse<HolidayCalendarDTO>> removeHolidayFromCalendar(@PathVariable String id, @PathVariable String holidayId) {
        HolidayCalendarDTO updated = calendarService.removeHolidayFromCalendar(id, holidayId);
        return ResponseEntity.ok(ApiResponse.success(updated, "Holiday removed from calendar successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holiday Calendar by ID")
    public ResponseEntity<ApiResponse<HolidayCalendarDTO>> getCalendarById(@PathVariable String id) {
        HolidayCalendarDTO result = calendarService.getCalendarById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Holiday calendar retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Holiday Calendars", description = "Retrieves calendars by year or branch")
    public ResponseEntity<ApiResponse<List<HolidayCalendarDTO>>> getCalendars(@RequestParam(required = false) Integer year) {
        int selectedYear = year != null ? year : Year.now().getValue();
        List<HolidayCalendarDTO> calendars = calendarService.getCalendarsByYear(selectedYear);
        return ResponseEntity.ok(ApiResponse.success(calendars, "Holiday calendars retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('HOLIDAY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_HOLIDAY_CALENDAR", module = "HOLIDAY")
    @Operation(summary = "Delete Holiday Calendar")
    public ResponseEntity<ApiResponse<Void>> deleteCalendar(@PathVariable String id) {
        calendarService.deleteCalendar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Holiday calendar deleted successfully"));
    }
}
