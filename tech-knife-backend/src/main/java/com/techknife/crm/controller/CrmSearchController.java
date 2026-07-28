package com.techknife.crm.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.crm.dto.CrmSearchDTO;
import com.techknife.crm.service.CrmSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crm/search")
@RequiredArgsConstructor
@Tag(name = "CRM - Search", description = "Global Cross-Entity Search Endpoint for CRM")
@SecurityRequirement(name = "bearerAuth")
public class CrmSearchController {

    private final CrmSearchService crmSearchService;

    @GetMapping
    @PreAuthorize("hasAuthority('LEAD_VIEW') or hasAuthority('CUSTOMER_VIEW') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Search across CRM Leads, Customers, Opportunities, Quotations, Proposals, and Contracts")
    public ResponseEntity<ApiResponse<CrmSearchDTO>> search(@RequestParam String query) {
        CrmSearchDTO result = crmSearchService.search(query);
        return ResponseEntity.ok(ApiResponse.success(result, "Search results retrieved successfully"));
    }

}
