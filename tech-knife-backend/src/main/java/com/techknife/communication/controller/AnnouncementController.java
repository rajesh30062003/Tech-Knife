package com.techknife.communication.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.communication.dto.AnnouncementCategoryDTO;
import com.techknife.communication.dto.AnnouncementDTO;
import com.techknife.communication.dto.AnnouncementReadDTO;
import com.techknife.communication.service.AnnouncementService;
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
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Tag(name = "Communication - Announcements", description = "Company Announcements API")
@SecurityRequirement(name = "bearerAuth")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "Announcement", description = "Created Announcement")
    @Operation(summary = "Create Announcement")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> createAnnouncement(@Valid @RequestBody AnnouncementDTO dto) {
        AnnouncementDTO result = announcementService.createAnnouncement(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created announcement successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "Announcement", description = "Updated Announcement")
    @Operation(summary = "Update Announcement")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> updateAnnouncement(
            @PathVariable String id,
            @Valid @RequestBody AnnouncementDTO dto) {
        AnnouncementDTO result = announcementService.updateAnnouncement(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated announcement successfully"));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "Announcement", description = "Published Announcement")
    @Operation(summary = "Publish Announcement")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> publishAnnouncement(@PathVariable String id) {
        AnnouncementDTO result = announcementService.publishAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Published announcement successfully"));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "Announcement", description = "Archived Announcement")
    @Operation(summary = "Archive Announcement")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> archiveAnnouncement(@PathVariable String id) {
        AnnouncementDTO result = announcementService.archiveAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Archived announcement successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "Announcement", description = "Deleted Announcement")
    @Operation(summary = "Delete Announcement")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(@PathVariable String id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted announcement successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Announcement by ID")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> getAnnouncementById(
            @PathVariable String id,
            @RequestParam(required = false) String userId) {
        AnnouncementDTO result = announcementService.getAnnouncementById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched announcement successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getAllAnnouncements(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String userId) {
        List<AnnouncementDTO> result = announcementService.getAllAnnouncements(status, categoryId, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched announcements successfully"));
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark Announcement as Read")
    public ResponseEntity<ApiResponse<AnnouncementReadDTO>> markAsRead(
            @PathVariable String id,
            @RequestParam String userId) {
        AnnouncementReadDTO result = announcementService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(result, "Marked announcement as read"));
    }

    // Categories
    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.COMMUNICATION, entityType = "AnnouncementCategory", description = "Created Announcement Category")
    @Operation(summary = "Create Announcement Category")
    public ResponseEntity<ApiResponse<AnnouncementCategoryDTO>> createCategory(@Valid @RequestBody AnnouncementCategoryDTO dto) {
        AnnouncementCategoryDTO result = announcementService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created category successfully"));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.COMMUNICATION, entityType = "AnnouncementCategory", description = "Updated Announcement Category")
    @Operation(summary = "Update Announcement Category")
    public ResponseEntity<ApiResponse<AnnouncementCategoryDTO>> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody AnnouncementCategoryDTO dto) {
        AnnouncementCategoryDTO result = announcementService.updateCategory(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated category successfully"));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get All Announcement Categories")
    public ResponseEntity<ApiResponse<List<AnnouncementCategoryDTO>>> getAllCategories() {
        List<AnnouncementCategoryDTO> result = announcementService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched categories successfully"));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.COMMUNICATION, entityType = "AnnouncementCategory", description = "Deleted Announcement Category")
    @Operation(summary = "Delete Announcement Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable String id) {
        announcementService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Deleted category successfully"));
    }
}
