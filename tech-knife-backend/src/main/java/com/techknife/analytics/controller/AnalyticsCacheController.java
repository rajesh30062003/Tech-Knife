package com.techknife.analytics.controller;

import com.techknife.analytics.service.AnalyticsCacheService;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/cache")
@RequiredArgsConstructor
@Tag(name = "Analytics - Cache Management", description = "Analytics Performance & Caching Management API")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsCacheController {

    private final AnalyticsCacheService cacheService;

    @DeleteMapping("/group/{cacheGroup}")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ANALYTICS, entityType = "AnalyticsCache", description = "Cache Group Invalidated")
    @Operation(summary = "Invalidate Cache Group")
    public ResponseEntity<ApiResponse<Void>> invalidateGroup(@PathVariable String cacheGroup) {
        cacheService.invalidateGroup(cacheGroup);
        return ResponseEntity.ok(ApiResponse.success(null, "Cache group invalidated successfully"));
    }

    @DeleteMapping("/expired")
    @PreAuthorize("hasAuthority('ANALYTICS_MANAGE') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.DELETE, module = AuditModule.ANALYTICS, entityType = "AnalyticsCache", description = "Expired Cache Cleared")
    @Operation(summary = "Clear Expired Analytics Cache Entries")
    public ResponseEntity<ApiResponse<Void>> clearExpiredCache() {
        cacheService.clearExpiredCache();
        return ResponseEntity.ok(ApiResponse.success(null, "Expired cache entries cleared successfully"));
    }
}
