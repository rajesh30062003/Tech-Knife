package com.techknife.github.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.github.dto.GitHubDashboardSummaryDTO;
import com.techknife.github.dto.GitHubSearchResponseDTO;
import com.techknife.github.service.GitHubDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Dashboard & Analytics", description = "Endpoints for GitHub Overview, Analytics, and Global Search")
@SecurityRequirement(name = "bearerAuth")
public class GitHubDashboardController {

    private final GitHubDashboardService dashboardService;

    @GetMapping("/dashboard/summary")
    @PreAuthorize("hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get GitHub Dashboard Overview (Repos, PRs, Issues, Commits, Deployments, Contributors)")
    public ResponseEntity<ApiResponse<GitHubDashboardSummaryDTO>> getDashboardSummary(
            @RequestParam(required = false) String repositoryId) {
        GitHubDashboardSummaryDTO summary = dashboardService.getDashboardSummary(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Dashboard summary retrieved successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Global GitHub Entity Search (Repos, Commits, PRs, Issues, Contributors, Branches, Releases)")
    public ResponseEntity<ApiResponse<GitHubSearchResponseDTO>> search(@RequestParam String query) {
        GitHubSearchResponseDTO result = dashboardService.search(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Search results retrieved successfully"));
    }
}
