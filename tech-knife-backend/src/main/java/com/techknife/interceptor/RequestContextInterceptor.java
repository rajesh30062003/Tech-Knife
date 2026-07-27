package com.techknife.interceptor;

import com.techknife.filter.CorrelationIdFilter;
import com.techknife.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC HandlerInterceptor capturing request context attributes into ThreadLocal state for thread-safe access.
 */
@Slf4j
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<RequestContextHolder> CONTEXT_HOLDER = new ThreadLocal<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestContextHolder {
        private String currentUser;
        private String clientIp;
        private String correlationId;
        private String requestUri;
    }

    /**
     * Obtains the current thread's RequestContextHolder object.
     *
     * @return RequestContextHolder instance or null if unassigned
     */
    public static RequestContextHolder getCurrentContext() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * Helper method to obtain the current request's Correlation ID.
     */
    public static String getCorrelationId() {
        RequestContextHolder context = CONTEXT_HOLDER.get();
        return context != null ? context.getCorrelationId() : null;
    }

    /**
     * Helper method to obtain the current request's authenticated User ID.
     */
    public static String getCurrentUser() {
        RequestContextHolder context = CONTEXT_HOLDER.get();
        return context != null ? context.getCurrentUser() : "ANONYMOUS";
    }

    /**
     * Helper method to obtain the current request's Client IP Address.
     */
    public static String getClientIp() {
        RequestContextHolder context = CONTEXT_HOLDER.get();
        return context != null ? context.getClientIp() : null;
    }

    /**
     * Helper method to obtain the current Request URI.
     */
    public static String getRequestUri() {
        RequestContextHolder context = CONTEXT_HOLDER.get();
        return context != null ? context.getRequestUri() : null;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = (String) request.getAttribute(CorrelationIdFilter.CORRELATION_ID_REQUEST_ATTRIBUTE);
        String clientIp = getClientIpAddress(request);
        String currentUser = "ANONYMOUS";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            currentUser = principal.getId();
        } else if (auth != null && StringUtils.hasText(auth.getName()) && !"anonymousUser".equals(auth.getName())) {
            currentUser = auth.getName();
        }

        RequestContextHolder context = RequestContextHolder.builder()
                .currentUser(currentUser)
                .clientIp(clientIp)
                .correlationId(correlationId)
                .requestUri(request.getRequestURI())
                .build();

        CONTEXT_HOLDER.set(context);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CONTEXT_HOLDER.remove();
    }

    private String getClientIpAddress(HttpServletRequest request) {
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
