package com.techknife.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.backend.dto.ApiResponse;
import com.techknife.backend.dto.ErrorDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Unauthorized request error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorDetails error = ErrorDetails.builder()
                .code("UNAUTHORIZED")
                .details(authException.getMessage())
                .path(request.getRequestURI())
                .build();

        ApiResponse<Void> apiResponse = ApiResponse.error("Full authentication is required to access this resource", error);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
