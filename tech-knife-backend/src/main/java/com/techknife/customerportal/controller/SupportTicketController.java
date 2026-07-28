package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.SupportTicketDTO;
import com.techknife.customerportal.dto.TicketReplyDTO;
import com.techknife.customerportal.service.SupportTicketService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/tickets")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Support Tickets", description = "Endpoints for Support Ticket Lifecycle (Creation, Replies, Status, Reopen, Close)")
@SecurityRequirement(name = "bearerAuth")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Support Tickets")
    public ResponseEntity<ApiResponse<List<SupportTicketDTO>>> getTickets(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String status) {
        List<SupportTicketDTO> result = supportTicketService.getTickets(userPrincipal.getId(), status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched tickets successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Support Ticket Details by ID")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> getTicketById(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        SupportTicketDTO result = supportTicketService.getTicketById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched ticket details successfully"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SupportTicket", description = "Created Support Ticket")
    @Operation(summary = "Create a Support Ticket with Attachments")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> createTicket(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestPart("data") SupportTicketDTO dto,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        SupportTicketDTO result = supportTicketService.createTicket(userPrincipal.getId(), dto, files);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created support ticket successfully"));
    }

    @PostMapping(value = "/{id}/replies", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_REPLY') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SupportTicket", description = "Replied to Support Ticket")
    @Operation(summary = "Add Reply to Support Ticket")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> addReply(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id,
            @Valid @RequestPart("data") TicketReplyDTO replyDTO,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        SupportTicketDTO result = supportTicketService.addReply(id, userPrincipal.getId(), replyDTO, files);
        return ResponseEntity.ok(ApiResponse.success(result, "Added ticket reply successfully"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SupportTicket", description = "Updated Ticket Status")
    @Operation(summary = "Update Ticket Status")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> updateStatus(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String resolutionNotes) {
        SupportTicketDTO result = supportTicketService.updateTicketStatus(id, userPrincipal.getId(), status, resolutionNotes);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated ticket status successfully"));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SupportTicket", description = "Closed Support Ticket")
    @Operation(summary = "Close Support Ticket")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> closeTicket(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        SupportTicketDTO result = supportTicketService.closeTicket(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Closed ticket successfully"));
    }

    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasAuthority('CUSTOMER_TICKET_CREATE') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SupportTicket", description = "Reopened Support Ticket")
    @Operation(summary = "Reopen Support Ticket")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> reopenTicket(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        SupportTicketDTO result = supportTicketService.reopenTicket(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Reopened ticket successfully"));
    }
}
