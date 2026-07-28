package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.KnowledgeArticleDTO;
import com.techknife.customerportal.dto.KnowledgeCategoryDTO;
import com.techknife.customerportal.service.KnowledgeBaseService;
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
@RequestMapping("/api/v1/customer/knowledge-base")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Knowledge Base", description = "Self-service Knowledge Base Articles, Categories, Search, and FAQ")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @GetMapping("/articles")
    @Operation(summary = "Get Published Knowledge Base Articles")
    public ResponseEntity<ApiResponse<List<KnowledgeArticleDTO>>> getArticles(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Boolean popular,
            @RequestParam(required = false) String query) {
        List<KnowledgeArticleDTO> result = knowledgeBaseService.getArticles(categoryId, popular, query);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched articles successfully"));
    }

    @GetMapping("/articles/{slug}")
    @Operation(summary = "Get Knowledge Base Article by Slug")
    public ResponseEntity<ApiResponse<KnowledgeArticleDTO>> getArticleBySlug(@PathVariable String slug) {
        KnowledgeArticleDTO result = knowledgeBaseService.getArticleBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched article details successfully"));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get Knowledge Base Categories")
    public ResponseEntity<ApiResponse<List<KnowledgeCategoryDTO>>> getCategories() {
        List<KnowledgeCategoryDTO> result = knowledgeBaseService.getCategories();
        return ResponseEntity.ok(ApiResponse.success(result, "Fetched categories successfully"));
    }

    @PostMapping("/articles/{id}/helpful")
    @Operation(summary = "Mark Article as Helpful")
    public ResponseEntity<ApiResponse<KnowledgeArticleDTO>> markHelpful(@PathVariable String id) {
        KnowledgeArticleDTO result = knowledgeBaseService.markHelpful(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Marked article as helpful"));
    }

    @PostMapping("/articles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "KnowledgeBase", description = "Created KB Article")
    @Operation(summary = "Create Knowledge Base Article (Admin Endpoint)")
    public ResponseEntity<ApiResponse<KnowledgeArticleDTO>> createArticle(@Valid @RequestBody KnowledgeArticleDTO dto) {
        KnowledgeArticleDTO result = knowledgeBaseService.createArticle(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created article successfully"));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "KnowledgeCategory", description = "Created KB Category")
    @Operation(summary = "Create Knowledge Base Category (Admin Endpoint)")
    public ResponseEntity<ApiResponse<KnowledgeCategoryDTO>> createCategory(@Valid @RequestBody KnowledgeCategoryDTO dto) {
        KnowledgeCategoryDTO result = knowledgeBaseService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created category successfully"));
    }

}
