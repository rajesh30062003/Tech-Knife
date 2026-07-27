package com.techknife.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC HandlerInterceptor measuring precise controller handler execution duration and flagging slow operations.
 */
@Slf4j
@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

    public static final String START_TIME_ATTRIBUTE = "REQUEST_START_TIME_MS";

    @Value("${logging.request.slow-threshold-ms:1000}")
    private long slowThresholdMs;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Object startTimeObj = request.getAttribute(START_TIME_ATTRIBUTE);

        if (startTimeObj instanceof Long startTime) {
            long duration = System.currentTimeMillis() - startTime;
            if (duration >= slowThresholdMs) {
                log.warn("[SLOW CONTROLLER HANDLER] URI: '{} {}' | Handler: {} | Duration: {}ms (Threshold: {}ms)",
                        request.getMethod(),
                        request.getRequestURI(),
                        handler != null ? handler.toString() : "Unknown",
                        duration,
                        slowThresholdMs);
            }
        }
    }
}
