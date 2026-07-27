package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.TaskAttachmentDTO;
import com.techknife.project.service.TaskAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/attachments")
@RequiredArgsConstructor
@Tag(name = "Task Attachments", description = "Endpoints for Task File Attachments (Documents, Images, ZIP, PDF)")
@SecurityRequirement(name = "bearerAuth")
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    @PostMapping(value = "/task/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UPLOAD_TASK_ATTACHMENT", module = "PROJECT")
    @Operation(summary = "Upload File Attachment to Task")
    public ResponseEntity<ApiResponse<TaskAttachmentDTO>> uploadAttachment(
            @PathVariable String taskId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String uploader = authentication != null ? authentication.getName() : "SYSTEM";
        TaskAttachmentDTO response = attachmentService.uploadAttachment(taskId, file, uploader);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Attachment uploaded successfully"));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Attachments by Task")
    public ResponseEntity<ApiResponse<List<TaskAttachmentDTO>>> getAttachmentsByTask(@PathVariable String taskId) {
        List<TaskAttachmentDTO> attachments = attachmentService.getAttachmentsByTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(attachments, "Task attachments retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Attachments by Project")
    public ResponseEntity<ApiResponse<List<TaskAttachmentDTO>>> getAttachmentsByProject(@PathVariable String projectId) {
        List<TaskAttachmentDTO> attachments = attachmentService.getAttachmentsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(attachments, "Project task attachments retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_TASK_ATTACHMENT", module = "PROJECT")
    @Operation(summary = "Delete Task Attachment")
    public ResponseEntity<ApiResponse<Void>> deleteAttachment(@PathVariable String id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Attachment deleted successfully"));
    }
}
