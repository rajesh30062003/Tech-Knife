package com.techknife.communication.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.*;
import com.techknife.communication.service.InternalMessageService;
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
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Communication - Messaging", description = "Internal Messaging API")
@SecurityRequirement(name = "bearerAuth")
public class InternalMessageController {

    private final InternalMessageService messageService;

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('MESSAGE_SEND') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "InternalMessage", description = "Sent Internal Message")
    @Operation(summary = "Send Internal Message")
    public ResponseEntity<ApiResponse<InternalMessageDTO>> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestParam String senderId,
            @RequestParam(required = false, defaultValue = "User") String senderName) {
        InternalMessageDTO result = messageService.sendMessage(request, senderId, senderName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Message sent successfully"));
    }

    @GetMapping("/threads/{threadId}")
    @PreAuthorize("hasAuthority('MESSAGE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Thread by ID")
    public ResponseEntity<ApiResponse<MessageThreadDTO>> getThreadById(@PathVariable String threadId) {
        MessageThreadDTO result = messageService.getThreadById(threadId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched thread successfully"));
    }

    @GetMapping("/threads/user/{userId}")
    @PreAuthorize("hasAuthority('MESSAGE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get User Message Threads")
    public ResponseEntity<ApiResponse<List<MessageThreadDTO>>> getUserThreads(@PathVariable String userId) {
        List<MessageThreadDTO> result = messageService.getUserThreads(userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched user message threads successfully"));
    }

    @GetMapping("/threads/{threadId}/messages")
    @PreAuthorize("hasAuthority('MESSAGE_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Thread Messages")
    public ResponseEntity<ApiResponse<List<InternalMessageDTO>>> getThreadMessages(
            @PathVariable String threadId,
            @RequestParam(required = false) String userId) {
        List<InternalMessageDTO> result = messageService.getThreadMessages(threadId, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched thread messages successfully"));
    }

    @PutMapping("/{messageId}/read")
    @Operation(summary = "Mark Message as Read")
    public ResponseEntity<ApiResponse<InternalMessageDTO>> markMessageAsRead(
            @PathVariable String messageId,
            @RequestParam String userId) {
        InternalMessageDTO result = messageService.markMessageAsRead(messageId, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Marked message as read"));
    }
}
