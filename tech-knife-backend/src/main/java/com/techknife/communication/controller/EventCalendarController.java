package com.techknife.communication.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.CalendarEventDTO;
import com.techknife.communication.dto.EventCalendarDTO;
import com.techknife.communication.service.EventCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar-events")
@RequiredArgsConstructor
@Tag(name = "Communication - Calendar & Events", description = "Event Calendar Management API")
@SecurityRequirement(name = "bearerAuth")
public class EventCalendarController {

    private final EventCalendarService calendarService;

    // Calendars
    @PostMapping("/calendars")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "EventCalendar", description = "Created Calendar")
    @Operation(summary = "Create Event Calendar")
    public ResponseEntity<ApiResponse<EventCalendarDTO>> createCalendar(@Valid @RequestBody EventCalendarDTO dto) {
        EventCalendarDTO result = calendarService.createCalendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created calendar successfully"));
    }

    @PutMapping("/calendars/{id}")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "EventCalendar", description = "Updated Calendar")
    @Operation(summary = "Update Event Calendar")
    public ResponseEntity<ApiResponse<EventCalendarDTO>> updateCalendar(
            @PathVariable String id,
            @Valid @RequestBody EventCalendarDTO dto) {
        EventCalendarDTO result = calendarService.updateCalendar(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated calendar successfully"));
    }

    @GetMapping("/calendars/{id}")
    @Operation(summary = "Get Calendar by ID")
    public ResponseEntity<ApiResponse<EventCalendarDTO>> getCalendarById(@PathVariable String id) {
        EventCalendarDTO result = calendarService.getCalendarById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched calendar successfully"));
    }

    @GetMapping("/calendars/user/{userId}")
    @Operation(summary = "Get User Calendars")
    public ResponseEntity<ApiResponse<List<EventCalendarDTO>>> getUserCalendars(@PathVariable String userId) {
        List<EventCalendarDTO> result = calendarService.getUserCalendars(userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user calendars successfully"));
    }

    @DeleteMapping("/calendars/{id}")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "EventCalendar", description = "Deleted Calendar")
    @Operation(summary = "Delete Event Calendar")
    public ResponseEntity<ApiResponse<Void>> deleteCalendar(@PathVariable String id) {
        calendarService.deleteCalendar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted calendar successfully"));
    }

    // Events
    @PostMapping("/events")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "CalendarEvent", description = "Created Calendar Event")
    @Operation(summary = "Create Calendar Event")
    public ResponseEntity<ApiResponse<CalendarEventDTO>> createEvent(@Valid @RequestBody CalendarEventDTO dto) {
        CalendarEventDTO result = calendarService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created event successfully"));
    }

    @PutMapping("/events/{id}")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "CalendarEvent", description = "Updated Calendar Event")
    @Operation(summary = "Update Calendar Event")
    public ResponseEntity<ApiResponse<CalendarEventDTO>> updateEvent(
            @PathVariable String id,
            @Valid @RequestBody CalendarEventDTO dto) {
        CalendarEventDTO result = calendarService.updateEvent(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated event successfully"));
    }

    @GetMapping("/events/{id}")
    @Operation(summary = "Get Event by ID")
    public ResponseEntity<ApiResponse<CalendarEventDTO>> getEventById(@PathVariable String id) {
        CalendarEventDTO result = calendarService.getEventById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched event successfully"));
    }

    @GetMapping("/events/calendar/{calendarId}")
    @Operation(summary = "Get Events by Calendar")
    public ResponseEntity<ApiResponse<List<CalendarEventDTO>>> getEventsByCalendar(@PathVariable String calendarId) {
        List<CalendarEventDTO> result = calendarService.getEventsByCalendar(calendarId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched calendar events successfully"));
    }

    @GetMapping("/events/range")
    @Operation(summary = "Get Events by Date Range")
    public ResponseEntity<ApiResponse<List<CalendarEventDTO>>> getEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        List<CalendarEventDTO> result = calendarService.getEventsByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched events by date range successfully"));
    }

    @DeleteMapping("/events/{id}")
    @PreAuthorize("hasAuthority('EVENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "CalendarEvent", description = "Deleted Calendar Event")
    @Operation(summary = "Delete Calendar Event")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable String id) {
        calendarService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted event successfully"));
    }
}
