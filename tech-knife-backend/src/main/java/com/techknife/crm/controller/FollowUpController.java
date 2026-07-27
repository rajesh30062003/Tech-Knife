package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.FollowUpDTO;
import com.techknife.crm.service.FollowUpService;
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
@RequestMapping("/api/v1/crm/followups")
@RequiredArgsConstructor
@Tag(name = "CRM - Follow-Ups", description = "Endpoints for managing CRM Follow-Up Reminders")
@SecurityRequirement(name = "bearerAuth")
public class FollowUpController {

    private final FollowUpService followUpService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all follow-ups")
    public ResponseEntity<ApiResponse<List<FollowUpDTO>>> getAllFollowUps(@RequestParam(required = false) String status) {
        List<FollowUpDTO> result = followUpService.getAllFollowUps(status);
        return ResponseEntity.ok(ApiResponse.success("Fetched follow-ups successfully", result));
    }

    @GetMapping("/entity")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get follow-ups for a specific entity")
    public ResponseEntity<ApiResponse<List<FollowUpDTO>>> getFollowUpsByEntity(
            @RequestParam String entityType,
            @RequestParam String entityId) {
        List<FollowUpDTO> result = followUpService.getFollowUpsByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success("Fetched entity follow-ups successfully", result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get follow-up by ID")
    public ResponseEntity<ApiResponse<FollowUpDTO>> getFollowUpById(@PathVariable String id) {
        FollowUpDTO result = followUpService.getFollowUpById(id);
        return ResponseEntity.ok(ApiResponse.success("Fetched follow-up successfully", result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "FollowUp", description = "Created CRM Follow-Up")
    @Operation(summary = "Create a new follow-up reminder")
    public ResponseEntity<ApiResponse<FollowUpDTO>> createFollowUp(@Valid @RequestBody FollowUpDTO dto) {
        FollowUpDTO result = followUpService.createFollowUp(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Created follow-up successfully", result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "FollowUp", description = "Updated CRM Follow-Up")
    @Operation(summary = "Update follow-up details or status")
    public ResponseEntity<ApiResponse<FollowUpDTO>> updateFollowUp(
            @PathVariable String id,
            @Valid @RequestBody FollowUpDTO dto) {
        FollowUpDTO result = followUpService.updateFollowUp(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Updated follow-up successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "FollowUp", description = "Deleted CRM Follow-Up")
    @Operation(summary = "Delete follow-up")
    public ResponseEntity<ApiResponse<Void>> deleteFollowUp(@PathVariable String id) {
        followUpService.deleteFollowUp(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted follow-up successfully", null));
    }
}
