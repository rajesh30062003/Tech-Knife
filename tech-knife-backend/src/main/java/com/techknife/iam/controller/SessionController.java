package com.techknife.iam.controller;

import com.techknife.backend.response.ApiResponse;
import com.techknife.iam.dto.response.LoginHistoryResponse;
import com.techknife.iam.dto.response.SessionResponse;
import com.techknife.iam.service.SessionService;
import com.techknife.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for active user session management, remote device session revocation, and security login audit log inspection.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Session API", description = "Endpoints for managing active sessions, revoking device access, and querying login security history")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/sessions")
    @Operation(summary = "Get Active Sessions", description = "Retrieve list of active device login sessions for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active user sessions retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getActiveSessions(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<SessionResponse> sessions = sessionService.getActiveSessions(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(sessions, "Active user sessions retrieved successfully"));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Terminate Specific Session", description = "Revoke access and terminate a specific active login session by session ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Session terminated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access or session does not belong to authenticated user"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Session not found")
    })
    public ResponseEntity<ApiResponse<Void>> terminateSession(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable String sessionId) {
        sessionService.terminateSession(currentUser.getId(), sessionId);
        return ResponseEntity.ok(ApiResponse.success(null, "Session terminated successfully"));
    }

    @DeleteMapping("/sessions")
    @Operation(summary = "Terminate All Sessions", description = "Revoke access and terminate all active login sessions across all devices for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All user sessions terminated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<Void>> terminateAllSessions(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        sessionService.terminateAllSessions(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null, "All active sessions terminated successfully"));
    }

    @GetMapping("/login-history")
    @Operation(summary = "Get Login History", description = "Retrieve paginated login security audit trail history for the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login history log page retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<ApiResponse<Page<LoginHistoryResponse>>> getLoginHistory(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PageableDefault(sort = "loginTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LoginHistoryResponse> history = sessionService.getLoginHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(history, "Login history log page retrieved successfully"));
    }
}
