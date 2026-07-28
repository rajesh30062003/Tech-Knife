package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.CommunicationLogDTO;
import com.techknife.crm.service.CommunicationLogService;
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
@RequestMapping("/api/v1/crm/communication-logs")
@RequiredArgsConstructor
@Tag(name = "CRM - Communication Logs", description = "Endpoints for tracking communication activities (Calls, Emails, Notes, etc.)")
@SecurityRequirement(name = "bearerAuth")
public class CommunicationLogController {

    private final CommunicationLogService communicationLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get communication logs for an entity (Lead, Customer, Opportunity)")
    public ResponseEntity<ApiResponse<List<CommunicationLogDTO>>> getLogsByEntity(
            @RequestParam String entityType,
            @RequestParam String entityId) {
        List<CommunicationLogDTO> result = communicationLogService.getLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched communication logs successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('LEAD_UPDATE') or hasAuthority('CUSTOMER_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "CommunicationLog", description = "Created Communication Log")
    @Operation(summary = "Log a communication entry")
    public ResponseEntity<ApiResponse<CommunicationLogDTO>> createLog(@Valid @RequestBody CommunicationLogDTO dto) {
        CommunicationLogDTO result = communicationLogService.createLog(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created communication log successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LEAD_DELETE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "CommunicationLog", description = "Deleted Communication Log")
    @Operation(summary = "Delete communication log")
    public ResponseEntity<ApiResponse<Void>> deleteLog(@PathVariable String id) {
        communicationLogService.deleteLog(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted communication log successfully"));
    }

}
