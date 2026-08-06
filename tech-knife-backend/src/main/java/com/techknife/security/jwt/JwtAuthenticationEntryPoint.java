package com.techknife.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.backend.response.ApiError;
import com.techknife.backend.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * AuthenticationEntryPoint handling unauthenticated REST API requests by returning a structured 401 Unauthorized response.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.info("==> [SecurityTrace] AuthenticationEntryPoint invoked for URI='{}', ServletPath='{}', Exception='{}'",
                request.getRequestURI(), request.getServletPath(), authException.getMessage());
        log.error("Authentication failure for request '{}': {}", request.getRequestURI(), authException.getMessage());

        new RuntimeException("401 Investigation").printStackTrace();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiError error = ApiError.builder()
                .code("UNAUTHORIZED")
                .details(authException.getMessage() != null ? authException.getMessage() : "Full authentication is required to access this resource")
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.error("Authentication Failed", error);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
