package com.techknife.security.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration properties wrapper for JWT security tokens.
 * Properties are injected from environment variables or application configuration with production fallback defaults.
 */
@Data
@Component
public class JwtProperties {

    @Value("${app.jwt.secret:9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b}")
    private String secret;

    @Value("${app.jwt.refresh-secret:8a7b6c5d4e3f2a1b0c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a7b}")
    private String refreshSecret;

    @Value("${app.jwt.access-expiration-ms:86400000}")
    private long accessExpirationMs; // 24 hours default

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs; // 7 days default

    @Value("${app.jwt.issuer:tech-knife-enterprise}")
    private String issuer;
}
