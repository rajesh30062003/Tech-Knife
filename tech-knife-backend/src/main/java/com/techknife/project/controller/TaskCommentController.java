package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.TaskCommentDTO;
import com.techknife.project.service.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks/comments")
@RequiredArgsConstructor
@Tag(name = "Task Comments", description = "Endpoints for Task Comments and Discussion")
@SecurityRequirement(name = "bearerAuth")
public class TaskCommentController {

    private final TaskCommentService commentService;

    @PostMapping
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CREATE_TASK_COMMENT", module = "PROJECT")
    @Operation(summary = "Add Comment to Task")
    public ResponseEntity<ApiResponse<TaskCommentDTO>> createComment(
            @Valid @RequestBody TaskCommentDTO dto,
            Authentication authentication) {
        String author = authentication != null ? authentication.getName() : "SYSTEM";
        TaskCommentDTO response = commentService.createComment(dto, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Comment added successfully"));
    }

    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAuthority('PROJECT_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Comments for Task")
    public ResponseEntity<ApiResponse<List<TaskCommentDTO>>> getCommentsByTask(@PathVariable String taskId) {
        List<TaskCommentDTO> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(comments, "Task comments retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TASK_UPDATE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_TASK_COMMENT", module = "PROJECT")
    @Operation(summary = "Delete Comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
    }
}
