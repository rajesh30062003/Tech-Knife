package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

/**
 * Response payload returned upon successful authentication login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response payload containing JWT tokens and user details")
public class LoginResponse {

    @Schema(description = "JWT Access Token for API authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "JWT Refresh Token for renewing access tokens", example = "d9f8e7d6c5b4a321...")
    private String refreshToken;

    @Builder.Default
    @Schema(description = "Token type scheme", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration timestamp")
    private Instant expiresIn;

    @Schema(description = "Authenticated user profile details")
    private UserProfileResponse userProfile;

    @Schema(description = "Set of assigned role codes", example = "[\"ROLE_ADMIN\", \"ROLE_EMPLOYEE\"]")
    private Set<String> roles;

    @Schema(description = "Set of granted permission codes", example = "[\"employee:read\", \"employee:write\"]")
    private Set<String> permissions;
}
