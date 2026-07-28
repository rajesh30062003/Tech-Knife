package com.techknife.customerportal.controller;

import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.entity.AuditAction;
import com.techknife.audit.entity.AuditModule;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.customerportal.dto.*;
import com.techknife.customerportal.service.CustomerAuthService;
import com.techknife.security.CurrentUser;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer/auth")
@RequiredArgsConstructor
@Tag(name = "Customer Portal - Authentication", description = "Self-service Customer Login, Registration, Password Reset, and Verification")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    @PostMapping("/login")
    @Auditable(action = AuditAction.LOGIN, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerAccount", description = "Customer Login")
    @Operation(summary = "Customer Portal Login")
    public ResponseEntity<ApiResponse<CustomerLoginResponse>> login(@Valid @RequestBody CustomerLoginRequest request) {
        CustomerLoginResponse response = customerAuthService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/register")
    @Auditable(action = AuditAction.CREATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerAccount", description = "Customer Registration")
    @Operation(summary = "Register new Customer Account")
    public ResponseEntity<ApiResponse<CustomerLoginResponse>> register(@Valid @RequestBody CustomerRegisterRequest request) {
        CustomerLoginResponse response = customerAuthService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Customer account registered successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate Forgot Password flow")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody CustomerForgotPasswordRequest request) {
        customerAuthService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset instructions sent to email"));
    }

    @PostMapping("/reset-password")
    @Auditable(action = AuditAction.RESET_PASSWORD, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerAccount", description = "Reset Password")
    @Operation(summary = "Reset Password using Token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody CustomerResetPasswordRequest request) {
        customerAuthService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"));
    }

    @GetMapping("/verify-email")
    @Auditable(action = AuditAction.VERIFY_EMAIL, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerAccount", description = "Verify Email")
    @Operation(summary = "Verify Email Address")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        customerAuthService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAuthority('CUSTOMER_PORTAL_ACCESS') or hasRole('ROLE_CUSTOMER') or hasRole('ROLE_ADMIN')")
    @Auditable(action = AuditAction.UPDATE, module = AuditModule.CUSTOMER_PORTAL, entityType = "CustomerAccount", description = "Change Password")
    @Operation(summary = "Change Password for Authenticated Customer")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @CurrentUser UserPrincipal userPrincipal,
            @Valid @RequestBody CustomerChangePasswordRequest request) {
        customerAuthService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

}
