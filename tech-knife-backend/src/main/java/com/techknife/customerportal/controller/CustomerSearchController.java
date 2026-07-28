package com.techknife.customerportal.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerSearchDTO;
import com.techknife.customerportal.service.CustomerSearchService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
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
@RequestMapping("/api/v1/customer/search")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Search", description = "Global Portal Search (Projects, Tickets, Invoices, Documents, Knowledge Base)")
@SecurityRequirement(name = "bearerAuth")
public class CustomerSearchController {

    private final CustomerSearchService customerSearchService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Global Portal Search across Projects, Tickets, Invoices, Documents, and Knowledge Base")
    public ResponseEntity<ApiResponse<CustomerSearchDTO>> search(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam String query) {
        CustomerSearchDTO result = customerSearchService.search(userPrincipal.getId(), query);
        return ResponseEntity.ok(ApiResponse.success(result, "Search completed successfully"));
    }
}
