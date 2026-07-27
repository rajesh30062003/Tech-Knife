package com.techknife.filter;

import com.techknife.logging.RequestLog;
import com.techknife.logging.RequestLoggingService;
import com.techknife.security.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Filter intercepting incoming HTTP calls to capture execution statistics and dispatch secure audit logs.
 */
@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> EXCLUDED_PATTERNS = Arrays.asList(
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**",
            "/favicon.ico",
            "/**/*.css",
            "/**/*.js",
            "/**/*.png",
            "/**/*.jpg",
            "/**/*.jpeg",
            "/**/*.gif",
            "/**/*.svg",
            "/**/*.ico",
            "/**/*.html"
    );

    private static final Pattern SENSITIVE_QUERY_PARAM_PATTERN = Pattern.compile(
            "(?i)(password|secret|token|jwt|refreshtoken|otp|pin|authorization|key|access_token)=[^&]*"
    );

    private final RequestLoggingService requestLoggingService;

    @Value("${logging.request.enabled:true}")
    private boolean enabled;

    @Value("${logging.request.slow-threshold-ms:1000}")
    private long slowThresholdMs;

    @Value("${logging.request.include-query-params:true}")
    private boolean includeQueryParams;

    @Value("${logging.request.include-response-size:true}")
    private boolean includeResponseSize;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }

        String path = request.getRequestURI();
        for (String pattern : EXCLUDED_PATTERNS) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        Instant timestamp = Instant.now();

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            try {
                recordRequestLog(request, responseWrapper, timestamp, executionTime);
            } catch (Exception ex) {
                log.error("Error generating HTTP request trace log: {}", ex.getMessage());
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void recordRequestLog(HttpServletRequest request, ContentCachingResponseWrapper response, Instant timestamp, long executionTime) {
        String correlationId = (String) request.getAttribute(CorrelationIdFilter.CORRELATION_ID_REQUEST_ATTRIBUTE);

        String queryParams = null;
        if (includeQueryParams && StringUtils.hasText(request.getQueryString())) {
            queryParams = sanitizeQueryString(request.getQueryString());
        }

        String clientIp = getClientIp(request);
        String userId = "ANONYMOUS";
        String userRole = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            userId = principal.getId();
            if (principal.getRoles() != null && !principal.getRoles().isEmpty()) {
                userRole = String.join(",", principal.getRoles());
            }
        } else if (auth != null && StringUtils.hasText(auth.getName()) && !"anonymousUser".equals(auth.getName())) {
            userId = auth.getName();
        }

        long responseSize = 0L;
        if (includeResponseSize) {
            responseSize = response.getContentSize();
        }

        RequestLog requestLog = RequestLog.builder()
                .timestamp(timestamp)
                .correlationId(correlationId)
                .method(request.getMethod())
                .requestUri(request.getRequestURI())
                .queryParams(queryParams)
                .clientIp(clientIp)
                .userId(userId)
                .userRole(userRole)
                .status(response.getStatus())
                .executionTimeMs(executionTime)
                .responseSize(responseSize)
                .slowRequest(executionTime >= slowThresholdMs)
                .userAgent(request.getHeader("User-Agent"))
                .build();

        requestLoggingService.logAsync(requestLog);
    }

    private String sanitizeQueryString(String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return queryString;
        }
        return SENSITIVE_QUERY_PARAM_PATTERN.matcher(queryString).replaceAll("$1=[REDACTED]");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
