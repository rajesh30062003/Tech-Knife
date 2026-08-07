package com.techknife.project.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.dto.SubTaskDTO;
import com.techknife.project.dto.TaskRequestDTO;
import com.techknife.project.dto.TaskResponseDTO;
import com.techknife.project.entity.TaskPriority;
import com.techknife.project.entity.TaskStatus;
import com.techknife.project.service.TaskService;
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
@RequestMapping({"/api/v1/projects/tasks", "/api/projects/tasks", "/projects/tasks"})
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Endpoints for Task & SubTask management")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "CREATE_TASK", module = "PROJECT")
    @Operation(summary = "Create Task")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> createTask(@Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Task created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "UPDATE_TASK", module = "PROJECT")
    @Operation(summary = "Update Task")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequestDTO request) {
        TaskResponseDTO response = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Task updated successfully"));
    }

    @PatchMapping("/{id}/assign")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "ASSIGN_TASK", module = "PROJECT")
    @Operation(summary = "Assign Task to Employee/Reviewer")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> assignTask(
            @PathVariable String id,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String reviewerId) {
        TaskResponseDTO response = taskService.assignTask(id, employeeId, reviewerId);
        return ResponseEntity.ok(ApiResponse.success(response, "Task assigned successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Task by ID")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskById(@PathVariable String id) {
        TaskResponseDTO response = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Task details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List / Filter Tasks")
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getTasks(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String assignedId,
            @RequestParam(required = false) String milestoneId) {
        List<TaskResponseDTO> tasks = taskService.getTasks(projectId, status, priority, assignedId, milestoneId);
        return ResponseEntity.ok(ApiResponse.success(tasks, "Tasks retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "DELETE_TASK", module = "PROJECT")
    @Operation(summary = "Delete Task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Task deleted successfully"));
    }

    // SubTask Endpoints
    @PostMapping("/{id}/subtasks")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "ADD_SUBTASK", module = "PROJECT")
    @Operation(summary = "Add SubTask")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> addSubTask(
            @PathVariable String id,
            @Valid @RequestBody SubTaskDTO subTaskDTO) {
        TaskResponseDTO response = taskService.addSubTask(id, subTaskDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Subtask added successfully"));
    }

    @PutMapping("/{id}/subtasks/{subTaskId}")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "UPDATE_SUBTASK", module = "PROJECT")
    @Operation(summary = "Update SubTask")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateSubTask(
            @PathVariable String id,
            @PathVariable String subTaskId,
            @Valid @RequestBody SubTaskDTO subTaskDTO) {
        TaskResponseDTO response = taskService.updateSubTask(id, subTaskId, subTaskDTO);
        return ResponseEntity.ok(ApiResponse.success(response, "Subtask updated successfully"));
    }

    @DeleteMapping("/{id}/subtasks/{subTaskId}")
    @PreAuthorize("isAuthenticated()")
    @Auditable(action = "DELETE_SUBTASK", module = "PROJECT")
    @Operation(summary = "Delete SubTask")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> deleteSubTask(
            @PathVariable String id,
            @PathVariable String subTaskId) {
        TaskResponseDTO response = taskService.deleteSubTask(id, subTaskId);
        return ResponseEntity.ok(ApiResponse.success(response, "Subtask deleted successfully"));
    }
}

