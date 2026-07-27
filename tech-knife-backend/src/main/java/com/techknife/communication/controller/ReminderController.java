package com.techknife.communication.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.ReminderDTO;
import com.techknife.communication.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
@Tag(name = "Communication - Reminders", description = "Personal & Task Reminders API")
@SecurityRequirement(name = "bearerAuth")
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "Reminder", description = "Created Reminder")
    @Operation(summary = "Create Reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> createReminder(@Valid @RequestBody ReminderDTO dto) {
        ReminderDTO result = reminderService.createReminder(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created reminder successfully"));
    }

    @PutMapping("/{id}")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "Reminder", description = "Updated Reminder")
    @Operation(summary = "Update Reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> updateReminder(
            @PathVariable String id,
            @Valid @RequestBody ReminderDTO dto) {
        ReminderDTO result = reminderService.updateReminder(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated reminder successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mark Reminder Status")
    public ResponseEntity<ApiResponse<ReminderDTO>> markStatus(
            @PathVariable String id,
            @RequestParam String status) {
        ReminderDTO result = reminderService.markStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated reminder status"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Reminder by ID")
    public ResponseEntity<ApiResponse<ReminderDTO>> getReminderById(@PathVariable String id) {
        ReminderDTO result = reminderService.getReminderById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched reminder successfully"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get User Reminders")
    public ResponseEntity<ApiResponse<List<ReminderDTO>>> getUserReminders(
            @PathVariable String userId,
            @RequestParam(required = false) String status) {
        List<ReminderDTO> result = reminderService.getUserReminders(userId, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user reminders successfully"));
    }

    @DeleteMapping("/{id}")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "Reminder", description = "Deleted Reminder")
    @Operation(summary = "Delete Reminder")
    public ResponseEntity<ApiResponse<Void>> deleteReminder(@PathVariable String id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted reminder successfully"));
    }
}
