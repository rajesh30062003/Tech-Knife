package com.techknife.backend.security;

import com.techknife.backend.constant.Permission;
import com.techknife.backend.constant.Role;
import com.techknife.backend.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.issuer:tech-knife-backend}")
    private String jwtIssuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Set<String> permissions = new HashSet<>();
        for (GrantedAuthority authority : userPrincipal.getAuthorities()) {
            try {
                Role role = Role.valueOf(authority.getAuthority());
                getPermissionsForRole(role).forEach(p -> permissions.add(p.name()));
            } catch (Exception ignored) {
            }
        }

        return Jwts.builder()
                .subject(userPrincipal.getId())
                .claim(SecurityConstants.EMAIL_KEY, userPrincipal.getEmail())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private Set<Permission> getPermissionsForRole(Role role) {
        Set<Permission> permissions = new HashSet<>();
        if (role == null) return permissions;

        switch (role) {
            case ROLE_SUPER_ADMIN, ROLE_ADMIN, ROLE_CEO, ROLE_CTO, ROLE_CMO, ROLE_MD, ROLE_DIRECTOR:
                permissions.addAll(Arrays.asList(Permission.values()));
                break;
            case ROLE_MANAGER, ROLE_PROJECT_LEAD:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PROJECT_WRITE,
                        Permission.CRM_READ, Permission.CRM_WRITE, Permission.RECRUITMENT_READ
                ));
                break;
            case ROLE_EMPLOYEE, ROLE_INTERN:
                permissions.addAll(Arrays.asList(
                        Permission.USER_READ, Permission.PROJECT_READ, Permission.PAYROLL_READ
                ));
                break;
            case ROLE_CUSTOMER:
                permissions.add(Permission.PROJECT_READ);
                break;
            default:
                permissions.add(Permission.USER_READ);
        }
        return permissions;
    }
}
