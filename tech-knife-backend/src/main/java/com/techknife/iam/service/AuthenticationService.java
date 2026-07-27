package com.techknife.iam.service;

import com.techknife.iam.dto.request.LoginRequest;
import com.techknife.iam.dto.request.LogoutRequest;
import com.techknife.iam.dto.request.RefreshTokenRequest;
import com.techknife.iam.dto.response.CurrentUserResponse;
import com.techknife.iam.dto.response.LoginResponse;
import com.techknife.iam.dto.response.RefreshTokenResponse;

/**
 * Service contract for core authentication workflows including login, token refresh, logout, and token validation.
 */
public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    LoginResponse login(LoginRequest request, String ipAddress, String userAgent);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);

    void logoutAllDevices(String userId);

    CurrentUserResponse getCurrentUser(String userId);

    boolean validateAccessToken(String token);

    boolean validateRefreshToken(String token);
}
