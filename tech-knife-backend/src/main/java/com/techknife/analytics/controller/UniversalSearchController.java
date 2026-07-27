package com.techknife.analytics.controller;

import com.techknife.analytics.dto.SearchIndexDTO;
import com.techknife.analytics.dto.UniversalSearchRequest;
import com.techknife.analytics.dto.UniversalSearchResponse;
import com.techknife.analytics.entity.SearchEntityType;
import com.techknife.analytics.service.UniversalSearchService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analytics/search")
@RequiredArgsConstructor
@Tag(name = "Analytics - Universal Search", description = "Cross-Module Enterprise Search Engine API")
@SecurityRequirement(name = "bearerAuth")
public class UniversalSearchController {

    private final UniversalSearchService searchService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('UNIVERSAL_SEARCH', 'ANALYTICS_VIEW') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.EXPORT, module = AuditModule.ANALYTICS, entityType = "UniversalSearch", description = "Search Query Executed")
    @Operation(summary = "Perform Universal Search")
    public ResponseEntity<ApiResponse<UniversalSearchResponse>> search(
            @Valid @RequestBody UniversalSearchRequest request,
            Authentication authentication) {
        String user = authentication != null ? authentication.getName() : "ANONYMOUS";
        UniversalSearchResponse response = searchService.search(request, user);
        return ResponseEntity.ok(ApiResponse.success(response, "Search executed successfully"));
    }

    @PostMapping("/index")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Index Document/Entity for Universal Search")
    public ResponseEntity<ApiResponse<SearchIndexDTO>> indexEntity(@Valid @RequestBody SearchIndexDTO dto) {
        SearchIndexDTO indexed = searchService.indexEntity(dto);
        return ResponseEntity.ok(ApiResponse.success(indexed, "Entity indexed successfully"));
    }

    @DeleteMapping("/index/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Remove Entity from Search Index")
    public ResponseEntity<ApiResponse<Void>> removeIndex(@PathVariable SearchEntityType entityType, @PathVariable String entityId) {
        searchService.removeIndex(entityId, entityType);
        return ResponseEntity.ok(ApiResponse.success(null, "Index entry removed successfully"));
    }

    @PostMapping("/reindex-all")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.ANALYTICS, entityType = "SearchIndex", description = "Reindex All Enterprise Entities")
    @Operation(summary = "Trigger Full System Reindex")
    public ResponseEntity<ApiResponse<Void>> reindexAll() {
        searchService.reindexAll();
        return ResponseEntity.ok(ApiResponse.success(null, "Full reindex completed successfully"));
    }
}
