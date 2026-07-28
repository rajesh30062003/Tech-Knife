package com.techknife.crm.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.ContractDTO;
import com.techknife.crm.service.ContractService;
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
@RequestMapping("/api/v1/crm/contracts")
@RequiredArgsConstructor
@Tag(name = "CRM - Contract Management", description = "Endpoints for managing CRM Contracts")
@SecurityRequirement(name = "bearerAuth")
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all contracts")
    public ResponseEntity<ApiResponse<List<ContractDTO>>> getAllContracts(@RequestParam(required = false) String status) {
        List<ContractDTO> result = contractService.getAllContracts(status);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched contracts successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get contract by ID")
    public ResponseEntity<ApiResponse<ContractDTO>> getContractById(@PathVariable String id) {
        ContractDTO result = contractService.getContractById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched contract successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CRM, entityType = "Contract", description = "Created CRM Contract")
    @Operation(summary = "Create a new contract")
    public ResponseEntity<ApiResponse<ContractDTO>> createContract(@Valid @RequestBody ContractDTO dto) {
        ContractDTO result = contractService.createContract(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created contract successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CRM, entityType = "Contract", description = "Updated CRM Contract")
    @Operation(summary = "Update contract details")
    public ResponseEntity<ApiResponse<ContractDTO>> updateContract(
            @PathVariable String id,
            @Valid @RequestBody ContractDTO dto) {
        ContractDTO result = contractService.updateContract(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated contract successfully"));
    }

    @PostMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.CRM, entityType = "Contract", description = "Uploaded Contract Document")
    @Operation(summary = "Upload contract document")
    public ResponseEntity<ApiResponse<ContractDTO>> uploadContractDocument(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        ContractDTO result = contractService.uploadContractDocument(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Uploaded contract document successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CONTRACT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CRM, entityType = "Contract", description = "Deleted CRM Contract")
    @Operation(summary = "Delete contract")
    public ResponseEntity<ApiResponse<Void>> deleteContract(@PathVariable String id) {
        contractService.deleteContract(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted contract successfully"));
    }

}
