package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload to terminate user sessions and revoke refresh tokens.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for logging out and revoking session tokens")
public class LogoutRequest {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token to be invalidated")
    private String refreshToken;

    @Schema(description = "Flag to invalidate sessions across all user devices", example = "false")
    private Boolean allDevices;
}
