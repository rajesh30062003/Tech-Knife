package com.techknife.organization.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.PagedResponse;
import com.techknife.organization.dto.TeamRequest;
import com.techknife.organization.dto.TeamResponse;
import com.techknife.organization.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organization/teams")
@RequiredArgsConstructor
@Auditable(module = "Team Management")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Team API", description = "Endpoints for managing Organizational Teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasAuthority('TEAM_CREATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Create team", description = "Registers a new team.")
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse response = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Team created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEAM_UPDATE') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER')")
    @Operation(summary = "Update team", description = "Updates details of an existing team.")
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @Parameter(description = "Team ID") @PathVariable("id") String id,
            @Valid @RequestBody TeamRequest request) {
        TeamResponse response = teamService.updateTeam(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Team updated successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TEAM_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get team by ID", description = "Retrieves team details by ID.")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(
            @Parameter(description = "Team ID") @PathVariable("id") String id) {
        TeamResponse response = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Team retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('TEAM_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get team by Code", description = "Retrieves team details by code.")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamByCode(
            @Parameter(description = "Team Code") @PathVariable("code") String code) {
        TeamResponse response = teamService.getTeamByCode(code);
        return ResponseEntity.ok(ApiResponse.success(response, "Team retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TEAM_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "List all teams", description = "Retrieves paginated list of teams.")
    public ResponseEntity<ApiResponse<PagedResponse<TeamResponse>>> getAllTeams(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<TeamResponse> response = teamService.getAllTeams(page, size);
        return ResponseEntity.ok(ApiResponse.success(response, "Teams list retrieved successfully"));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('TEAM_VIEW') or hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get teams by department", description = "Lists teams belonging to a department.")
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeamsByDepartment(
            @Parameter(description = "Department ID") @PathVariable("departmentId") String departmentId) {
        List<TeamResponse> response = teamService.getTeamsByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success(response, "Teams list retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEAM_DELETE') or hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Delete team", description = "Removes a team record.")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(
            @Parameter(description = "Team ID") @PathVariable("id") String id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Team deleted successfully"));
    }
}
