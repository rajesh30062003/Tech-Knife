package com.techknife.github.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.github.dto.GitHubAccountDTO;
import com.techknife.github.entity.GitHubOrganization;
import com.techknife.github.service.GitHubAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/accounts")
@RequiredArgsConstructor
@Tag(name = "GitHub Account", description = "Endpoints for connecting and managing GitHub Accounts & Organizations")
@SecurityRequirement(name = "bearerAuth")
public class GitHubAccountController {

    private final GitHubAccountService accountService;

    @PostMapping("/connect")
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "CONNECT_GITHUB_ACCOUNT", module = "GITHUB")
    @Operation(summary = "Connect GitHub Account via OAuth / Personal Access Token")
    public ResponseEntity<ApiResponse<GitHubAccountDTO>> connectAccount(
            @Valid @RequestBody GitHubAccountDTO dto,
            Authentication authentication) {
        String connectedUser = authentication != null ? authentication.getName() : "SYSTEM";
        GitHubAccountDTO response = accountService.connectAccount(dto, connectedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "GitHub account connected successfully"));
    }

    @PostMapping("/disconnect/{username}")
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DISCONNECT_GITHUB_ACCOUNT", module = "GITHUB")
    @Operation(summary = "Disconnect GitHub Account")
    public ResponseEntity<ApiResponse<GitHubAccountDTO>> disconnectAccount(@PathVariable String username) {
        GitHubAccountDTO response = accountService.disconnectAccount(username);
        return ResponseEntity.ok(ApiResponse.success(response, "GitHub account disconnected successfully"));
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get GitHub Account Details")
    public ResponseEntity<ApiResponse<GitHubAccountDTO>> getAccountByUsername(@PathVariable String username) {
        GitHubAccountDTO response = accountService.getAccountByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(response, "GitHub account details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "List Connected GitHub Accounts")
    public ResponseEntity<ApiResponse<List<GitHubAccountDTO>>> getAllConnectedAccounts() {
        List<GitHubAccountDTO> accounts = accountService.getAllConnectedAccounts();
        return ResponseEntity.ok(ApiResponse.success(accounts, "Connected GitHub accounts retrieved successfully"));
    }

    @GetMapping("/{accountId}/organizations")
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get GitHub Organizations for Account")
    public ResponseEntity<ApiResponse<List<GitHubOrganization>>> getOrganizationsForAccount(@PathVariable String accountId) {
        List<GitHubOrganization> orgs = accountService.getOrganizationsForAccount(accountId);
        return ResponseEntity.ok(ApiResponse.success(orgs, "GitHub organizations retrieved successfully"));
    }

    @PostMapping("/validate-token")
    @PreAuthorize("hasAuthority('GITHUB_CONNECT') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Validate GitHub OAuth Token / PAT")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean valid = accountService.validateOAuthOrToken(token);
        return ResponseEntity.ok(ApiResponse.success(valid, valid ? "Token is valid" : "Token is invalid"));
    }
}
