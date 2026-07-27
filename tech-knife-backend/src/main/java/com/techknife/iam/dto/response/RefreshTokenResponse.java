package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response payload returned when renewing an access token using a refresh token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing new JWT access token details")
public class RefreshTokenResponse {

    @Schema(description = "Newly issued JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "Refresh token string", example = "d9f8e7d6c5b4a321...")
    private String refreshToken;

    @Builder.Default
    @Schema(description = "Token type scheme", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration timestamp")
    private Instant expiresIn;
}
