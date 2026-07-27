package com.techknife.iam.controller;

import com.techknife.backend.response.ApiResponse;
import com.techknife.iam.dto.request.ChangePasswordRequest;
import com.techknife.iam.dto.request.ForgotPasswordRequest;
import com.techknife.iam.dto.request.LoginRequest;
import com.techknife.iam.dto.request.LogoutRequest;
import com.techknife.iam.dto.request.RefreshTokenRequest;
import com.techknife.iam.dto.request.ResendVerificationRequest;
import com.techknife.iam.dto.request.ResetPasswordRequest;
import com.techknife.iam.dto.request.SendOtpRequest;
import com.techknife.iam.dto.request.VerifyEmailRequest;
import com.techknife.iam.dto.request.VerifyOtpRequest;
import com.techknife.iam.dto.response.CurrentUserResponse;
import com.techknife.iam.dto.response.LoginResponse;
import com.techknife.iam.dto.response.RefreshTokenResponse;
import com.techknife.iam.service.AuthenticationService;
import com.techknife.iam.service.EmailVerificationService;
import com.techknife.iam.service.OtpService;
import com.techknife.iam.service.PasswordService;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing authentication, password management, OTP verification, and email verification endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Core IAM REST endpoints for user login, token refresh, session revocation, OTP, and password lifecycle management")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordService passwordService;
    private final OtpService otpService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user credentials using official email and password. Generates access and refresh tokens upon success.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated user and returned JWT tokens"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload or validation failure"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid email or password / Account locked or disabled")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        LoginResponse response = authenticationService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success(response, "User authenticated successfully"));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Rotate Refresh Token", description = "Exchange a valid, non-expired refresh token for a new access token and rotated refresh token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully refreshed JWT tokens"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid, expired, or revoked refresh token")
    })
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "JWT token rotated and refreshed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout Active Session", description = "Revoke current refresh token and terminate active session.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully logged out active session"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "User logged out successfully"));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout All Devices", description = "Revoke all active refresh tokens and terminate all active user sessions.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully revoked all user sessions"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        authenticationService.logoutAllDevices(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all active devices successfully"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password Request", description = "Initiate password recovery flow and dispatch recovery instructions to official email.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password recovery token dispatched if account exists"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request email format")
    })
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        passwordService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset instructions sent to official email"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password with Token", description = "Reset account credentials using a valid, non-expired password reset token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account password reset successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid reset token or weak password policy violation")
    })
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        passwordService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Account password reset successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change Password", description = "Update account password for current authenticated user.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Incorrect current password or weak new password"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        passwordService.changePassword(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "Account password updated successfully"));
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP Challenge", description = "Generate and email a single-use multi-factor verification OTP code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verification OTP code sent to official email"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Missing user identifier or invalid target")
    })
    public ResponseEntity<ApiResponse<Void>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        otpService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Verification OTP sent successfully"));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP Code", description = "Validate single-use OTP code challenge.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP code verification result returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Maximum retry attempts exceeded or invalid request")
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        boolean verified = otpService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verified, verified ? "OTP code verified successfully" : "Invalid or expired OTP code"));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify Registration Email", description = "Confirm email address ownership using verification token.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email address verified successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired email verification token")
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {
        boolean verified = emailVerificationService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success(verified, "Email address verified successfully"));
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend Verification Email", description = "Dispatches a new email verification token to unverified account.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Verification email resent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email address is already verified or user not found")
    })
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request) {
        emailVerificationService.resendVerificationEmail(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Verification email resent successfully"));
    }

    @GetMapping("/current-user")
    @Operation(summary = "Get Current User Profile", description = "Retrieve active authenticated user details and assigned roles/permissions.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authenticated user profile fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<ApiResponse<CurrentUserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        CurrentUserResponse response = authenticationService.getCurrentUser(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Authenticated user details fetched successfully"));
    }
}
