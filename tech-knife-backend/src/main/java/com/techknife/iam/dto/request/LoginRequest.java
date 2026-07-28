package com.techknife.iam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * Request payload for user authentication login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for authenticating user credentials")
public class LoginRequest {

    @NotBlank(message = "Official email is required")
    @Email(message = "Invalid email format")
    @JsonAlias({"email", "username"})
    @Schema(description = "User's official organizational email", example = "john.doe@techknife.com")
    private String officialEmail;

    @NotBlank(message = "Password is required")
    @Schema(description = "Account password", example = "P@ssword123!")
    private String password;

    @Schema(description = "Option to extend refresh token longevity", example = "true")
    private Boolean rememberMe;

    @Schema(description = "Device information or User Agent string", example = "Chrome 120.0 on macOS")
    private String deviceInfo;
}
