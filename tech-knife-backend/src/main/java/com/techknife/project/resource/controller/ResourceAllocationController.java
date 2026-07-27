package com.techknife.project.resource.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.project.resource.dto.ResourceAllocationDTO;
import com.techknife.project.resource.service.ResourceAllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/project/resources")
@RequiredArgsConstructor
@Tag(name = "Resource Utilization", description = "Endpoints for Capacity Tracking, Resource Allocation, and Workload Utilization")
@SecurityRequirement(name = "bearerAuth")
public class ResourceAllocationController {

    private final ResourceAllocationService resourceAllocationService;

    @PostMapping
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create or Update Resource Capacity & Allocation")
    public ResponseEntity<ApiResponse<ResourceAllocationDTO>> createOrUpdateAllocation(@RequestBody ResourceAllocationDTO request) {
        ResourceAllocationDTO dto = resourceAllocationService.createOrUpdateAllocation(request);
        return ResponseEntity.ok(ApiResponse.success(dto, "Resource allocation saved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Resource Allocations by Project")
    public ResponseEntity<ApiResponse<List<ResourceAllocationDTO>>> getAllocationsByProject(@PathVariable String projectId) {
        List<ResourceAllocationDTO> list = resourceAllocationService.getAllocationsByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(list, "Project resource allocations retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Resource Allocations by Employee")
    public ResponseEntity<ApiResponse<List<ResourceAllocationDTO>>> getAllocationsByEmployee(@PathVariable String employeeId) {
        List<ResourceAllocationDTO> list = resourceAllocationService.getAllocationsByEmployee(employeeId);
        return ResponseEntity.ok(ApiResponse.success(list, "Employee resource allocations retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('RESOURCE_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get All Resource Allocations Across Organization")
    public ResponseEntity<ApiResponse<List<ResourceAllocationDTO>>> getAllAllocations() {
        List<ResourceAllocationDTO> list = resourceAllocationService.getAllAllocations();
        return ResponseEntity.ok(ApiResponse.success(list, "All resource allocations retrieved successfully"));
    }
}
