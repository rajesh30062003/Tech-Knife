package com.techknife.github.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.github.dto.GitHubSyncHistoryDTO;
import com.techknife.github.service.GitHubSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/sync")
@RequiredArgsConstructor
@Tag(name = "GitHub Sync", description = "Endpoints for Manual, Scheduled, and Incremental Synchronization")
@SecurityRequirement(name = "bearerAuth")
public class GitHubSyncController {

    private final GitHubSyncService syncService;

    @PostMapping("/repository/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_SYNC') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "TRIGGER_MANUAL_GITHUB_SYNC", module = "GITHUB")
    @Operation(summary = "Trigger Manual Synchronization for Repository")
    public ResponseEntity<ApiResponse<GitHubSyncHistoryDTO>> triggerManualSync(
            @PathVariable String repositoryId,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "SYSTEM";
        GitHubSyncHistoryDTO response = syncService.triggerManualSync(repositoryId, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Manual sync initiated successfully"));
    }

    @PostMapping("/history/{syncHistoryId}/retry")
    @PreAuthorize("hasAuthority('GITHUB_SYNC') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "RETRY_FAILED_GITHUB_SYNC", module = "GITHUB")
    @Operation(summary = "Retry Failed Sync Task")
    public ResponseEntity<ApiResponse<GitHubSyncHistoryDTO>> retryFailedSync(
            @PathVariable String syncHistoryId,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "SYSTEM";
        GitHubSyncHistoryDTO response = syncService.retryFailedSync(syncHistoryId, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Sync retry triggered successfully"));
    }

    @GetMapping("/history/repository/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_SYNC') or hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Sync History for Repository")
    public ResponseEntity<ApiResponse<List<GitHubSyncHistoryDTO>>> getSyncHistory(@PathVariable String repositoryId) {
        List<GitHubSyncHistoryDTO> history = syncService.getSyncHistory(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(history, "Sync history retrieved successfully"));
    }
}
