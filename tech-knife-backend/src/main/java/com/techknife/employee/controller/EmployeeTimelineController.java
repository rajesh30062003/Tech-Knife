package com.techknife.employee.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.employee.dto.EmployeeTimelineResponse;
import com.techknife.employee.service.EmployeeTimelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Lifecycle Timeline", description = "Track department, designation, salary grade, manager and branch changes")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeTimelineController {

    private final EmployeeTimelineService timelineService;

    @GetMapping("/{id}/timeline")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Employee Timeline History", description = "Retrieves complete audit trail of employee changes")
    public ResponseEntity<ApiResponse<List<EmployeeTimelineResponse>>> getTimelineForEmployee(@PathVariable String id) {
        List<EmployeeTimelineResponse> timeline = timelineService.getTimelineForEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(timeline, "Employee timeline history retrieved successfully"));
    }
}
