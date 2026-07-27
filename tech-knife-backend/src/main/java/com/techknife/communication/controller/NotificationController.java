package com.techknife.communication.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.*;
import com.techknife.communication.service.NotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Communication - Notifications", description = "Notification Management API")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND') or hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get User Notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(required = false) String status) {
        List<NotificationDTO> result = notificationService.getUserNotifications(userId, status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user notifications successfully"));
    }

    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND') or hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Unread Notification Count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count, "Fetched unread notification count"));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('NOTIFICATION_SEND') or hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "Notification", description = "Sent Notification")
    @Operation(summary = "Send Notification")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        NotificationDTO result = notificationService.sendNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Notification sent successfully"));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark Notification as Read")
    public ResponseEntity<ApiResponse<NotificationDTO>> markAsRead(
            @PathVariable String id,
            @RequestParam String userId) {
        NotificationDTO result = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Notification marked as read"));
    }

    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark All User Notifications as Read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "Notification", description = "Deleted Notification")
    @Operation(summary = "Delete Notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable String id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted notification successfully"));
    }

    // Templates
    @PostMapping("/templates")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "NotificationTemplate", description = "Created Notification Template")
    @Operation(summary = "Create Notification Template")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> createTemplate(@Valid @RequestBody NotificationTemplateDTO dto) {
        NotificationTemplateDTO result = notificationService.createTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Template created successfully"));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "NotificationTemplate", description = "Updated Notification Template")
    @Operation(summary = "Update Notification Template")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> updateTemplate(
            @PathVariable String id,
            @Valid @RequestBody NotificationTemplateDTO dto) {
        NotificationTemplateDTO result = notificationService.updateTemplate(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Template updated successfully"));
    }

    @GetMapping("/templates/code/{code}")
    @Operation(summary = "Get Template by Code")
    public ResponseEntity<ApiResponse<NotificationTemplateDTO>> getTemplateByCode(@PathVariable String code) {
        NotificationTemplateDTO result = notificationService.getTemplateByCode(code);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched template successfully"));
    }

    @GetMapping("/templates")
    @Operation(summary = "Get All Notification Templates")
    public ResponseEntity<ApiResponse<List<NotificationTemplateDTO>>> getAllTemplates() {
        List<NotificationTemplateDTO> result = notificationService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched templates successfully"));
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "NotificationTemplate", description = "Deleted Notification Template")
    @Operation(summary = "Delete Notification Template")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(@PathVariable String id) {
        notificationService.deleteTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted template successfully"));
    }

    // Preferences
    @GetMapping("/preferences/{userId}")
    @Operation(summary = "Get User Notification Preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> getPreference(@PathVariable String userId) {
        NotificationPreferenceDTO result = notificationService.getPreference(userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user preferences successfully"));
    }

    @PutMapping("/preferences/{userId}")
    @Operation(summary = "Update User Notification Preferences")
    public ResponseEntity<ApiResponse<NotificationPreferenceDTO>> updatePreference(
            @PathVariable String userId,
            @Valid @RequestBody NotificationPreferenceDTO dto) {
        NotificationPreferenceDTO result = notificationService.updatePreference(userId, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated user preferences successfully"));
    }

    // Queue
    @GetMapping("/queue")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Notification Queue")
    public ResponseEntity<ApiResponse<List<NotificationQueueDTO>>> getQueuedNotifications() {
        List<NotificationQueueDTO> result = notificationService.getQueuedNotifications();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched notification queue successfully"));
    }

    @PostMapping("/queue/{queueId}/retry")
    @PreAuthorize("hasAuthority('NOTIFICATION_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Retry Queued Notification")
    public ResponseEntity<ApiResponse<NotificationQueueDTO>> retryNotification(@PathVariable String queueId) {
        NotificationQueueDTO result = notificationService.retryNotification(queueId);
        return ResponseEntity.ok(ApiResponse.success(result, "Retried queued notification successfully"));
    }
}
