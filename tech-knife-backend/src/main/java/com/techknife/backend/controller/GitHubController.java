package com.techknife.backend.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.github.GitHubIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
@Tag(name = "GitHub Organization Integration", description = "Enterprise Repositories, Commit Activity and Webhook Sync")
public class GitHubController {

    private final GitHubIntegrationService gitHubIntegrationService;

    @PostMapping("/sync")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Trigger GitHub organization repository sync")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncRepositories() {
        Map<String, Object> result = gitHubIntegrationService.syncOrganizationRepositories();
        return ResponseEntity.ok(ApiResponse.success(result, "GitHub repository metadata synchronized"));
    }

    @GetMapping("/repos/{repoName}/commits")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Fetch commit history for specified enterprise repository")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCommits(@PathVariable("repoName") String repoName) {
        List<Map<String, Object>> commits = gitHubIntegrationService.getRecentCommits(repoName);
        return ResponseEntity.ok(ApiResponse.success(commits, "Recent commits fetched successfully"));
    }
}
