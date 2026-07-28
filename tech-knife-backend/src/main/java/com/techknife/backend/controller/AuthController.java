package com.techknife.backend.controller;

import com.techknife.backend.dto.*;
import com.techknife.security.UserPrincipal;
import com.techknife.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Module", description = "Official Email Authentication, JWT Token Management, OTP Verification & Credentials Lifecycle")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    @Operation(summary = "Reject GET login attempts", description = "Authentication requires HTTP POST with JSON body payload.")
    public ResponseEntity<ApiResponse<Void>> loginGet() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("HTTP Method GET is not supported for authentication. Please send credentials via HTTP POST /auth/login", null));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user using official email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new enterprise user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Enterprise account registered successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate refresh token and issue new JWT access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(response, "JWT token rotated and refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke refresh token and invalidate user session")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null && request.getRefreshToken() != null) {
            authService.logout(request.getRefreshToken());
        }
        return ResponseEntity.ok(ApiResponse.success(null, "User logged out successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Trigger password reset OTP dispatched to registered official email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset OTP sent to official email"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset account password using verified single-use OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Account password reset successfully"));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Dispatch email verification OTP")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendEmailVerificationOtp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Verification OTP sent to email"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP code for email address verification")
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean verified = authService.verifyEmailOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verified, "OTP verified successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated active user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password updated successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve active authenticated user details and mapped permissions")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserResponse response = authService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Authenticated user profile fetched successfully"));
    }

    @PatchMapping("/profile-picture")
    @Operation(summary = "Update profile avatar URL for authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ProfilePictureRequest request) {
        UserResponse response = authService.updateProfilePicture(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response, "Profile picture updated successfully"));
    }
}
