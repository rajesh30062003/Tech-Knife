package com.techknife.iam.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response payload representing active user session details across devices.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Active user session metadata")
public class SessionResponse {

    @Schema(description = "Session or token ID", example = "SESS-8912")
    private String id;

    @Schema(description = "User ID", example = "USR-2026-001")
    private String userId;

    @Schema(description = "User agent device information", example = "Chrome 120.0 on macOS")
    private String deviceInfo;

    @Schema(description = "Client IP address", example = "192.168.1.100")
    private String ipAddress;

    @Schema(description = "Geographic location string", example = "San Francisco, US")
    private String location;

    @Schema(description = "Session creation timestamp")
    private Instant createdTime;

    @Schema(description = "Last activity timestamp")
    private Instant lastAccessedTime;

    @Schema(description = "Session active status", example = "true")
    private boolean active;

    private Instant expiresAt;

    private boolean currentSession;

    public static class SessionResponseBuilder {
        public SessionResponseBuilder sessionId(String sessionId) {
            this.id = sessionId;
            return this;
        }

        public SessionResponseBuilder createdAt(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public SessionResponseBuilder lastActiveAt(Instant lastActiveAt) {
            this.lastAccessedTime = lastActiveAt;
            return this;
        }

        public SessionResponseBuilder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public SessionResponseBuilder currentSession(boolean currentSession) {
            this.currentSession = currentSession;
            this.active = currentSession;
            return this;
        }
    }




}

