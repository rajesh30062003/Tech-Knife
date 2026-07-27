package com.techknife.project.workflow.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.entity.Task;
import com.techknife.project.workflow.service.ProjectWorkflowService;
import com.techknife.timetracking.entity.Timesheet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflow Automation", description = "Endpoints for Auto-closing tasks, Overdue Alerts, Pending Timesheet Reminders, and Deadline Checkers")
@SecurityRequirement(name = "bearerAuth")
public class ProjectWorkflowController {

    private final ProjectWorkflowService workflowService;

    @PostMapping("/auto-close-tasks/project/{projectId}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Auto Close Reviewed Tasks for Project")
    public ResponseEntity<ApiResponse<Integer>> autoCloseTasks(@PathVariable String projectId) {
        int closed = workflowService.autoCloseCompletedTasks(projectId);
        return ResponseEntity.ok(ApiResponse.success(closed, "Auto closed " + closed + " tasks"));
    }

    @GetMapping("/pending-timesheet-reminders")
    @PreAuthorize("hasAuthority('TIME_TRACK_APPROVE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Check Draft Timesheets Requiring Reminders")
    public ResponseEntity<ApiResponse<List<Timesheet>>> checkPendingTimesheets() {
        List<Timesheet> list = workflowService.checkPendingTimesheetsForReminders();
        return ResponseEntity.ok(ApiResponse.success(list, "Pending timesheets for reminder retrieved"));
    }

    @GetMapping("/overdue-tasks/project/{projectId}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Overdue Tasks for Project Alerting")
    public ResponseEntity<ApiResponse<List<Task>>> checkOverdueTasks(@PathVariable String projectId) {
        List<Task> list = workflowService.checkOverdueTasks(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Overdue tasks retrieved successfully"));
    }

    @GetMapping("/deadline-alert/project/{projectId}")
    @PreAuthorize("hasAuthority('SPRINT_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Check if Project Deadline is Imminent (within 7 days)")
    public ResponseEntity<ApiResponse<Boolean>> checkDeadlineAlert(@PathVariable String projectId) {
        boolean alert = workflowService.checkProjectDeadlineAlert(projectId);
        return ResponseEntity.ok(ApiResponse.success(alert, "Deadline alert status retrieved"));
    }
}
