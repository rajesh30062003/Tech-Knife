package com.techknife.iam.dto.response;

import com.techknife.iam.enums.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Response payload indicating the active session authentication state and security parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Active session authentication state summary")
public class AuthenticationStatusResponse {

    @Schema(description = "Is caller currently authenticated", example = "true")
    private boolean authenticated;

    @Schema(description = "Authenticated user ID", example = "USR-2026-001")
    private String userId;

    @Schema(description = "Authenticated user email", example = "john.doe@techknife.com")
    private String email;

    @Schema(description = "Assigned security role codes")
    private Set<String> roles;

    @Schema(description = "Current account status")
    private AccountStatus accountStatus;

    @Schema(description = "Is multi-factor authentication required or pending", example = "false")
    private boolean mfaRequired;
}
