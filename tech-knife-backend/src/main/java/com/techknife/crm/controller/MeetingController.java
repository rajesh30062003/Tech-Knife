package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.MeetingDTO;
import com.techknife.crm.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crm/meetings")
@RequiredArgsConstructor
@Tag(name = "CRM - Meetings", description = "Endpoints for scheduling and logging CRM Meetings")
@SecurityRequirement(name = "bearerAuth")
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all meetings")
    public ResponseEntity<ApiResponse<List<MeetingDTO>>> getAllMeetings(@RequestParam(required = false) String status) {
        List<MeetingDTO> result = meetingService.getAllMeetings(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched meetings successfully"));
    }

    @GetMapping("/entity")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get meetings for a specific entity")
    public ResponseEntity<ApiResponse<List<MeetingDTO>>> getMeetingsByEntity(
            @RequestParam String entityType,
            @RequestParam String entityId) {
        List<MeetingDTO> result = meetingService.getMeetingsByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched entity meetings successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get meeting by ID")
    public ResponseEntity<ApiResponse<MeetingDTO>> getMeetingById(@PathVariable String id) {
        MeetingDTO result = meetingService.getMeetingById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched meeting successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Meeting", description = "Scheduled CRM Meeting")
    @Operation(summary = "Schedule a meeting")
    public ResponseEntity<ApiResponse<MeetingDTO>> createMeeting(@Valid @RequestBody MeetingDTO dto) {
        MeetingDTO result = meetingService.createMeeting(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Scheduled meeting successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "Meeting", description = "Updated CRM Meeting")
    @Operation(summary = "Update meeting details or outcome")
    public ResponseEntity<ApiResponse<MeetingDTO>> updateMeeting(
            @PathVariable String id,
            @Valid @RequestBody MeetingDTO dto) {
        MeetingDTO result = meetingService.updateMeeting(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated meeting successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Meeting", description = "Deleted CRM Meeting")
    @Operation(summary = "Delete meeting")
    public ResponseEntity<ApiResponse<Void>> deleteMeeting(@PathVariable String id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted meeting successfully"));
    }

}
