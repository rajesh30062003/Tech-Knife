package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response payload representing an audit trail record of a login attempt.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login audit trail record details")
public class LoginHistoryResponse {

    @Schema(description = "Audit record document ID")
    private String id;

    @Schema(description = "Target user ID")
    private String userId;

    @Schema(description = "Timestamp of login attempt")
    private Instant loginTime;

    @Schema(description = "Origin IP address")
    private String ipAddress;

    @Schema(description = "HTTP User-Agent header")
    private String userAgent;

    @Schema(description = "Parsed device information")
    private String deviceInfo;

    @Schema(description = "Geographic location tag")
    private String location;

    @Schema(description = "Attempt outcome status", example = "SUCCESS")
    private String status;

    @Schema(description = "Failure reason if attempt failed", example = "INVALID_CREDENTIALS")
    private String failureReason;
}
