package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for setting a new password using a reset token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for resetting password via token")
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    @Schema(description = "Password reset verification token")
    private String token;

    @NotBlank(message = "New password is required")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
        message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    @Schema(description = "New password satisfying security constraints", example = "Secure@2026Pass")
    private String newPassword;

    public String getResetToken() {
        return this.token;
    }
}

