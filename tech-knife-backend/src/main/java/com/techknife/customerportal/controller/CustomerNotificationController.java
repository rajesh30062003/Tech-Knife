package com.techknife.customerportal.controller;

import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.CustomerNotificationDTO;
import com.techknife.customerportal.service.CustomerNotificationService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/notifications")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Notifications", description = "Customer Notifications Management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerNotificationController {

    private final CustomerNotificationService customerNotificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get Customer Notifications")
    public ResponseEntity<ApiResponse<List<CustomerNotificationDTO>>> getNotifications(@CurrentUser UserPrincipal userPrincipal) {
        List<CustomerNotificationDTO> result = customerNotificationService.getNotifications(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Fetched notifications successfully", result));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Mark Notification as Read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable String id) {
        customerNotificationService.markAsRead(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }
}
