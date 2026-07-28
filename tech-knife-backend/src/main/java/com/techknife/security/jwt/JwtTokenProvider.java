package com.techknife.security.jwt;

import com.techknife.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility component responsible for generating, parsing, and validating JWT Access and Refresh Tokens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(org.springframework.security.core.Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateAccessToken(userPrincipal);
    }

    public String getUserIdFromJWT(String token) {
        return getUserIdFromToken(token);
    }

    /**
     * Generates a signed Access Token containing user claims, roles, and permissions.
     *
     * @param userPrincipal authenticated user principal
     * @return signed JWT Access Token
     */
    public String generateAccessToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessExpirationMs());
        String jti = UUID.randomUUID().toString();

        log.debug("Generating Access Token for userId '{}', jti '{}'", userPrincipal.getId(), jti);

        return Jwts.builder()
                .id(jti)
                .subject(userPrincipal.getId())
                .claim("userId", userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("roles", userPrincipal.getRoles())
                .claim("permissions", userPrincipal.getPermissions())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(jwtProperties.getSecret()))
                .compact();
    }

    /**
     * Generates a rotation-ready signed Refresh Token for the user.
     *
     * @param userPrincipal authenticated user principal
     * @return signed JWT Refresh Token
     */
    public String generateRefreshToken(UserPrincipal userPrincipal) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpirationMs());
        String jti = UUID.randomUUID().toString();

        log.debug("Generating Refresh Token for userId '{}', jti '{}'", userPrincipal.getId(), jti);

        return Jwts.builder()
                .id(jti)
                .subject(userPrincipal.getId())
                .claim("userId", userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("tokenType", "REFRESH")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(jwtProperties.getRefreshSecret()))
                .compact();
    }

    /**
     * Validates signature, structural integrity, and expiration of an Access Token.
     *
     * @param token JWT Access Token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        return validateTokenWithSecret(token, jwtProperties.getSecret(), "Access");
    }

    /**
     * Validates signature, structural integrity, and expiration of a Refresh Token.
     *
     * @param token JWT Refresh Token
     * @return true if valid, false otherwise
     */
    public boolean validateRefreshToken(String token) {
        return validateTokenWithSecret(token, jwtProperties.getRefreshSecret(), "Refresh");
    }

    private boolean validateTokenWithSecret(String token, String secretKey, String tokenType) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(secretKey))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid {} JWT signature: {}", tokenType, ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Malformed {} JWT token: {}", tokenType, ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired {} JWT token: {}", tokenType, ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported {} JWT token: {}", tokenType, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("{} JWT claims string is empty or invalid: {}", tokenType, ex.getMessage());
        }
        return false;
    }

    /**
     * Extracts Claims payload from Access Token.
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(jwtProperties.getSecret()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts Claims payload from Refresh Token.
     */
    public Claims getClaimsFromRefreshToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey(jwtProperties.getRefreshSecret()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String userId = claims.get("userId", String.class);
        return userId != null ? userId : claims.getSubject();
    }

    public String getUserIdFromRefreshToken(String token) {
        Claims claims = getClaimsFromRefreshToken(token);
        String userId = claims.get("userId", String.class);
        return userId != null ? userId : claims.getSubject();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).get("email", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Object rolesObj = getClaimsFromToken(token).get("roles");
        if (rolesObj instanceof Collection<?>) {
            return ((Collection<?>) rolesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        Object permsObj = getClaimsFromToken(token).get("permissions");
        if (permsObj instanceof Collection<?>) {
            return ((Collection<?>) permsObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
