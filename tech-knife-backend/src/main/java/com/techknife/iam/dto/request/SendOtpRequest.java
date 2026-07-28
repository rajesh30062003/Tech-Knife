package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload to trigger sending a multi-factor OTP code via SMS or Email.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload to request OTP generation and dispatch")
public class SendOtpRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "Target User ID", example = "USR-2026-001")
    private String userId;

    @NotBlank(message = "Purpose is required")
    @Schema(description = "Purpose of OTP (e.g. LOGIN_MFA, PASSWORD_RESET, MOBILE_VERIFICATION)", example = "LOGIN_MFA")
    private String purpose;

    @Pattern(
        regexp = "^$|^\\+?[1-9]\\d{1,14}$",
        message = "Invalid phone number format (E.164 standard)"
    )
    @Schema(description = "Target mobile number (optional if sending via registered phone)", example = "+12025550143")
    private String mobileNumber;

    private String email;

    public String getEmail() {
        return this.email != null ? this.email : this.userId;
    }
}

