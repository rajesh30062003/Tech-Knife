package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standard JWT token payload response container.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token container response")
public class TokenResponse {

    @Schema(description = "JWT Access token string")
    private String accessToken;

    @Schema(description = "Refresh token string")
    private String refreshToken;

    @Builder.Default
    @Schema(description = "Token authorization scheme type", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiry timestamp")
    private Instant expiresIn;
}
