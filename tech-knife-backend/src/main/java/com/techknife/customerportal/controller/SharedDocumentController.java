package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.SharedDocumentDTO;
import com.techknife.customerportal.service.SharedDocumentService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/documents")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Documents", description = "Cloudinary Shared Document Management")
@SecurityRequirement(name = "bearerAuth")
public class SharedDocumentController {

    private final SharedDocumentService sharedDocumentService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_DOCUMENT_DOWNLOAD') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Shared Documents")
    public ResponseEntity<ApiResponse<List<SharedDocumentDTO>>> getDocuments(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String category) {
        List<SharedDocumentDTO> result = sharedDocumentService.getDocuments(userPrincipal.getId(), projectId, category);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched documents successfully"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CUSTOMER_DOCUMENT_DOWNLOAD') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPLOAD, module = AuditModule.CUSTOMER_PORTAL, entityType = "SharedDocument", description = "Uploaded Shared Document")
    @Operation(summary = "Upload Document to Cloudinary")
    public ResponseEntity<ApiResponse<SharedDocumentDTO>> uploadDocument(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String documentName,
            @RequestParam(required = false) String description,
            @RequestPart("file") MultipartFile file) {
        SharedDocumentDTO result = sharedDocumentService.uploadDocument(userPrincipal.getId(), projectId, category, documentName, description, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Uploaded document successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER_DOCUMENT_DOWNLOAD') or hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.CUSTOMER_PORTAL, entityType = "SharedDocument", description = "Deleted Shared Document")
    @Operation(summary = "Delete Shared Document")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        sharedDocumentService.deleteDocument(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Deleted document successfully"));
    }

}
