package com.techknife.iam.service;

import com.techknife.iam.dto.response.LoginHistoryResponse;
import com.techknife.iam.dto.response.SessionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service contract for managing active user sessions, session revocations, and login audit logs.
 */
public interface SessionService {

    List<SessionResponse> getActiveSessions(String userId);

    void terminateSession(String userId, String sessionId);

    void terminateAllSessions(String userId);

    Page<LoginHistoryResponse> getLoginHistory(String userId, Pageable pageable);
}
