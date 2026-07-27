package com.techknife.github.controller;

import com.techknife.backend.audit.Auditable;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.github.dto.GitHubRepositoryDTO;
import com.techknife.github.service.GitHubRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/github/repositories")
@RequiredArgsConstructor
@Tag(name = "GitHub Repositories", description = "Endpoints for Linking, Unlinking, and Managing GitHub Repositories")
@SecurityRequirement(name = "bearerAuth")
public class GitHubRepositoryController {

    private final GitHubRepositoryService repositoryService;

    @PostMapping("/link")
    @PreAuthorize("hasAuthority('GITHUB_REPOSITORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "LINK_GITHUB_REPOSITORY", module = "GITHUB")
    @Operation(summary = "Link GitHub Repository to Project")
    public ResponseEntity<ApiResponse<GitHubRepositoryDTO>> linkRepository(
            @Valid @RequestBody GitHubRepositoryDTO dto,
            @RequestParam(required = false) String projectId) {
        GitHubRepositoryDTO response = repositoryService.linkRepository(dto, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Repository linked successfully"));
    }

    @PostMapping("/{repositoryId}/unlink")
    @PreAuthorize("hasAuthority('GITHUB_REPOSITORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "UNLINK_GITHUB_REPOSITORY", module = "GITHUB")
    @Operation(summary = "Unlink GitHub Repository")
    public ResponseEntity<ApiResponse<GitHubRepositoryDTO>> unlinkRepository(@PathVariable String repositoryId) {
        GitHubRepositoryDTO response = repositoryService.unlinkRepository(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(response, "Repository unlinked successfully"));
    }

    @GetMapping("/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_REPOSITORY_MANAGE') or hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Repository Metadata by ID")
    public ResponseEntity<ApiResponse<GitHubRepositoryDTO>> getRepositoryById(@PathVariable String repositoryId) {
        GitHubRepositoryDTO response = repositoryService.getRepositoryById(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(response, "Repository metadata retrieved successfully"));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Linked Repositories for Project")
    public ResponseEntity<ApiResponse<List<GitHubRepositoryDTO>>> getRepositoriesByProject(@PathVariable String projectId) {
        List<GitHubRepositoryDTO> repositories = repositoryService.getRepositoriesByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(repositories, "Project repositories retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('GITHUB_ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "List All Tracked Repositories")
    public ResponseEntity<ApiResponse<List<GitHubRepositoryDTO>>> getAllRepositories() {
        List<GitHubRepositoryDTO> repositories = repositoryService.getAllRepositories();
        return ResponseEntity.ok(ApiResponse.success(repositories, "Repositories retrieved successfully"));
    }

    @DeleteMapping("/{repositoryId}")
    @PreAuthorize("hasAuthority('GITHUB_REPOSITORY_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = "DELETE_GITHUB_REPOSITORY", module = "GITHUB")
    @Operation(summary = "Remove Repository Metadata")
    public ResponseEntity<ApiResponse<Void>> deleteRepository(@PathVariable String repositoryId) {
        repositoryService.deleteRepository(repositoryId);
        return ResponseEntity.ok(ApiResponse.success(null, "Repository deleted successfully"));
    }
}
