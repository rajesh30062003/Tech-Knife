package com.techknife.backend.audit;

import com.techknife.backend.entity.AuditLog;
import com.techknife.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Aspect
@Component("legacyAuditAspect")

@RequiredArgsConstructor
public class AuditAspect {

    @org.springframework.beans.factory.annotation.Qualifier("backendAuditLogRepository")
    private final AuditLogRepository auditLogRepository;


    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
        // Pointcut for REST controllers
    }

    @Around("restControllerPointcut()")
    public Object auditRestCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String principal = (authentication != null) ? authentication.getName() : "ANONYMOUS";

        Object result;
        String status = "SUCCESS";

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            status = "FAILED: " + ex.getMessage();
            throw ex;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            
            AuditLog auditLog = AuditLog.builder()
                    .principal(principal)
                    .action(methodName)
                    .module(className)
                    .method(joinPoint.getSignature().toShortString())
                    .status(status)
                    .executionTimeMs(executionTime)
                    .timestamp(Instant.now())
                    .build();

            try {
                auditLogRepository.save(auditLog);
            } catch (Exception e) {
                log.error("Failed to persist audit log: {}", e.getMessage());
            }
        }
    }
}
