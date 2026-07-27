package com.techknife.procurement.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.procurement.dto.PurchaseApprovalDTO;
import com.techknife.procurement.dto.PurchaseRequestDTO;
import com.techknife.procurement.service.PurchaseRequestService;
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
@RequestMapping("/api/v1/procurement/purchase-requests")
@RequiredArgsConstructor
@Tag(name = "Procurement - Purchase Requests", description = "Purchase Request API")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseRequestController {

    private final PurchaseRequestService requestService;

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Purchase Requests")
    public ResponseEntity<ApiResponse<List<PurchaseRequestDTO>>> getAllPurchaseRequests() {
        List<PurchaseRequestDTO> result = requestService.getAllPurchaseRequests();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase requests successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Purchase Request by ID")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> getPurchaseRequestById(@PathVariable String id) {
        PurchaseRequestDTO result = requestService.getPurchaseRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase request successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.PROCUREMENT, entityType = "PurchaseRequest", description = "Created Purchase Request")
    @Operation(summary = "Create Purchase Request")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> createPurchaseRequest(@Valid @RequestBody PurchaseRequestDTO dto) {
        PurchaseRequestDTO result = requestService.createPurchaseRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created purchase request successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.PROCUREMENT, entityType = "PurchaseRequest", description = "Updated Purchase Request")
    @Operation(summary = "Update Purchase Request")
    public ResponseEntity<ApiResponse<PurchaseRequestDTO>> updatePurchaseRequest(@PathVariable String id, @Valid @RequestBody PurchaseRequestDTO dto) {
        PurchaseRequestDTO result = requestService.updatePurchaseRequest(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated purchase request successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('PURCHASE_APPROVE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.APPROVE, module = AuditModule.PROCUREMENT, entityType = "PurchaseRequest", description = "Approved/Rejected Purchase Request")
    @Operation(summary = "Approve or Reject Purchase Request")
    public ResponseEntity<ApiResponse<PurchaseApprovalDTO>> approvePurchaseRequest(
            @PathVariable String id,
            @RequestParam String approverId,
            @RequestParam(required = false) String approverName,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments) {
        PurchaseApprovalDTO result = requestService.approvePurchaseRequest(id, approverId, approverName, approved, comments);
        return ResponseEntity.ok(ApiResponse.success(result, "Processed purchase request approval successfully"));
    }

    @GetMapping("/{id}/approvals")
    @PreAuthorize("hasAuthority('PURCHASE_REQUEST_CREATE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Purchase Request Approvals History")
    public ResponseEntity<ApiResponse<List<PurchaseApprovalDTO>>> getApprovalsByPurchaseRequest(@PathVariable String id) {
        List<PurchaseApprovalDTO> result = requestService.getApprovalsByPurchaseRequest(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched purchase request approval history"));
    }
}
