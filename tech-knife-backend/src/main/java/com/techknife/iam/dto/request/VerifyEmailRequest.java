package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for verifying an email address using a verification token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for email verification token validation")
public class VerifyEmailRequest {

    @NotBlank(message = "Verification token is required")
    @Schema(description = "Email verification token string")
    private String token;
}
