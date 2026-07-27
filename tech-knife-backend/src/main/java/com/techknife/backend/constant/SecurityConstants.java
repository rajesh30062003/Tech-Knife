package com.techknife.backend.constant;

public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORITIES_KEY = "roles";
    public static final String USER_ID_KEY = "userId";
    public static final String EMAIL_KEY = "email";

    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",
        "/api/v1/health/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/actuator/health",
        "/favicon.ico"
    };
}
