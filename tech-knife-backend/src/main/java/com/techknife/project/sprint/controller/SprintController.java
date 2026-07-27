package com.techknife.project.sprint.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.sprint.dto.*;
import com.techknife.project.sprint.service.SprintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project/sprints")
@RequiredArgsConstructor
@Tag(name = "Sprint Management", description = "Endpoints for Agile Sprint Planning, Boards, Reviews, and Retrospectives")
@SecurityRequirement(name = "bearerAuth")
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create New Sprint")
    public ResponseEntity<ApiResponse<SprintDTO>> createSprint(@RequestBody SprintDTO request) {
        SprintDTO dto = sprintService.createSprint(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Sprint created successfully"));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Start Sprint")
    public ResponseEntity<ApiResponse<SprintDTO>> startSprint(@PathVariable String id) {
        SprintDTO dto = sprintService.startSprint(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Sprint started successfully"));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Close / Complete Sprint")
    public ResponseEntity<ApiResponse<SprintDTO>> closeSprint(@PathVariable String id) {
        SprintDTO dto = sprintService.closeSprint(id);
        return ResponseEntity.ok(ApiResponse.success(dto, "Sprint completed successfully"));
    }

    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Assign Tasks / Stories to Sprint")
    public ResponseEntity<ApiResponse<SprintDTO>> assignTasksToSprint(@PathVariable String id, @RequestBody List<String> taskIds) {
        SprintDTO dto = sprintService.assignTasksToSprint(id, taskIds);
        return ResponseEntity.ok(ApiResponse.success(dto, "Tasks assigned to sprint successfully"));
    }

    @GetMapping("/{id}/board")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Interactive Sprint Kanban Board Data")
    public ResponseEntity<ApiResponse<SprintBoardDTO>> getSprintBoard(@PathVariable String id) {
        SprintBoardDTO board = sprintService.getSprintBoard(id);
        return ResponseEntity.ok(ApiResponse.success(board, "Sprint board retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Sprints for Project")
    public ResponseEntity<ApiResponse<List<SprintDTO>>> getSprintsByProject(@PathVariable String projectId) {
        List<SprintDTO> list = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project sprints retrieved successfully"));
    }

    @PostMapping("/reviews")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Save Sprint Review Details")
    public ResponseEntity<ApiResponse<SprintReviewDTO>> saveSprintReview(@RequestBody SprintReviewDTO request) {
        SprintReviewDTO dto = sprintService.saveSprintReview(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Sprint review saved successfully"));
    }

    @PostMapping("/retrospectives")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Save Sprint Retrospective Details")
    public ResponseEntity<ApiResponse<SprintRetrospectiveDTO>> saveSprintRetrospective(@RequestBody SprintRetrospectiveDTO request) {
        SprintRetrospectiveDTO dto = sprintService.saveSprintRetrospective(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Sprint retrospective saved successfully"));
    }
}
