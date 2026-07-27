package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for validating an OTP challenge code.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for validating OTP code")
public class VerifyOtpRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID", example = "USR-2026-001")
    private String userId;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 numeric digits")
    @Schema(description = "6-digit OTP code", example = "482910")
    private String otp;

    @NotBlank(message = "Purpose is required")
    @Schema(description = "Purpose of OTP challenge", example = "LOGIN_MFA")
    private String purpose;
}
