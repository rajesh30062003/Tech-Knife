package com.techknife.audit.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techknife.audit.annotation.Auditable;
import com.techknife.audit.dto.AuditLogRequest;
import com.techknife.audit.service.AuditService;
import com.techknife.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring AOP Aspect intercepting methods annotated with {@link Auditable} to asynchronously persist audit log entries.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditable)")
    public Object auditMethodExecution(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        long startTime = System.currentTimeMillis();
        Instant timestamp = Instant.now();
        String status = "SUCCESS";
        Object result = null;
        Throwable methodException = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            status = "FAILURE";
            methodException = ex;
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            try {
                recordAuditLog(joinPoint, auditable, timestamp, status, executionTime, result, methodException);
            } catch (Exception auditEx) {
                log.error("Failed to process AOP audit aspect for method '{}': {}",
                        joinPoint.getSignature().toShortString(), auditEx.getMessage(), auditEx);
            }
        }
    }

    private void recordAuditLog(ProceedingJoinPoint joinPoint,
                                Auditable auditable,
                                Instant timestamp,
                                String status,
                                long executionTime,
                                Object result,
                                Throwable exception) {

        AuditLogRequest.AuditLogRequestBuilder requestBuilder = AuditLogRequest.builder()
                .timestamp(timestamp)
                .action(auditable.action())
                .module(auditable.module())
                .entityType(auditable.entityType())
                .status(status)
                .executionTime(executionTime);

        // Extract description
        String description = auditable.description();
        if (exception != null) {
            description = (description != null && !description.isBlank() ? description + " - " : "") + "Error: " + exception.getMessage();
        } else if (description.isBlank()) {
            description = "Executed " + joinPoint.getSignature().getName();
        }
        requestBuilder.description(description);

        // Extract authenticated user context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            requestBuilder.userId(principal.getId());
            requestBuilder.userEmail(principal.getEmail());
            requestBuilder.userName(principal.getEmail());
            if (principal.getRoles() != null && !principal.getRoles().isEmpty()) {
                requestBuilder.userRole(String.join(",", principal.getRoles()));
            }
        } else if (auth != null && auth.getName() != null) {
            requestBuilder.userId(auth.getName());
            requestBuilder.userEmail(auth.getName());
        } else {
            requestBuilder.userId("ANONYMOUS");
        }

        // Extract HTTP servlet request metadata
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            requestBuilder.ipAddress(getClientIpAddress(request));
            requestBuilder.userAgent(request.getHeader("User-Agent"));
            requestBuilder.requestMethod(request.getMethod());
            requestBuilder.requestUri(request.getRequestURI());
            if (request.getSession(false) != null) {
                requestBuilder.sessionId(request.getSession(false).getId());
            }
        }

        // Capture input parameter as oldValue / args
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                List<Object> safeArgs = new ArrayList<>();
                for (Object arg : args) {
                    if (arg instanceof HttpServletRequest || arg instanceof jakarta.servlet.http.HttpServletResponse) {
                        continue;
                    }
                    safeArgs.add(arg);
                }
                if (!safeArgs.isEmpty()) {
                    requestBuilder.oldValue(objectMapper.writeValueAsString(safeArgs));
                }
            }
        } catch (Exception ex) {
            log.debug("Could not serialize method arguments for audit log: {}", ex.getMessage());
        }

        // Capture result as newValue
        if (result != null && !"FAILURE".equals(status)) {
            try {
                requestBuilder.newValue(objectMapper.writeValueAsString(result));
            } catch (Exception ex) {
                log.debug("Could not serialize method result for audit log: {}", ex.getMessage());
            }
        }

        // Asynchronously dispatch audit log saving
        auditService.logAsync(requestBuilder.build());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
