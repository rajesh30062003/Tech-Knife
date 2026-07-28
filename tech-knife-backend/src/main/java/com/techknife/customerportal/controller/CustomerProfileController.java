package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerProfileDTO;
import com.techknife.customerportal.service.CustomerProfileService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/profile")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Profile", description = "Customer Profile Management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Profile")
    public ResponseEntity<ApiResponse<CustomerProfileDTO>> getProfile(@CurrentUser UserPrincipal userPrincipal) {
        CustomerProfileDTO result = customerProfileService.getProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result, "Profile retrieved successfully"));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerProfile", description = "Updated Profile")
    @Operation(summary = "Update Customer Profile")
    public ResponseEntity<ApiResponse<CustomerProfileDTO>> updateProfile(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody CustomerProfileDTO dto) {
        CustomerProfileDTO result = customerProfileService.updateProfile(userPrincipal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Profile updated successfully"));
    }

}
