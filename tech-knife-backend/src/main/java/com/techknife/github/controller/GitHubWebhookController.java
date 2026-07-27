package com.techknife.github.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.github.dto.GitHubWebhookDTO;
import com.techknife.github.service.GitHubWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/webhooks")
@RequiredArgsConstructor
@Tag(name = "GitHub Webhooks", description = "Endpoints for Webhook Configuration and Receiver Processing")
public class GitHubWebhookController {

    private final GitHubWebhookService webhookService;

    @PostMapping("/repository/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_WEBHOOK_MANAGE') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Auditable(action = "CREATE_GITHUB_WEBHOOK", module = "GITHUB")
    @Operation(summary = "Register Webhook for Repository")
    public ResponseEntity<ApiResponse<GitHubWebhookDTO>> createWebhook(
            @PathVariable String repositoryId,
            @RequestParam(required = false) List<String> events,
            @RequestParam(required = false) String secret) {
        GitHubWebhookDTO response = webhookService.createWebhook(repositoryId, events, secret);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Webhook registered successfully"));
    }

    @GetMapping("/repository/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_WEBHOOK_MANAGE') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get Registered Webhooks for Repository")
    public ResponseEntity<ApiResponse<List<GitHubWebhookDTO>>> getWebhooksByRepository(@PathVariable String repositoryId) {
        List<GitHubWebhookDTO> webhooks = webhookService.getWebhooksByRepository(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(webhooks, "Webhooks retrieved successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GITHUB_WEBHOOK_MANAGE') or hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Auditable(action = "DELETE_GITHUB_WEBHOOK", module = "GITHUB")
    @Operation(summary = "Delete Webhook")
    public ResponseEntity<ApiResponse<Void>> deleteWebhook(@PathVariable String id) {
        webhookService.deleteWebhook(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Webhook deleted successfully"));
    }

    @PostMapping("/receiver/{repositoryId}")
    @Auditable(action = "PROCESS_GITHUB_WEBHOOK_EVENT", module = "GITHUB")
    @Operation(summary = "Inbound Webhook Receiver from GitHub")
    public ResponseEntity<ApiResponse<String>> receiveWebhook(
            @PathVariable String repositoryId,
            @RequestHeader(value = "X-GitHub-Event", defaultValue = "push") String eventType,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload) {

        webhookService.processWebhookEvent(repositoryId, eventType, payload);
        return ResponseEntity.ok(ApiResponse.success("Event processed", "Webhook event processed successfully"));
    }
}
