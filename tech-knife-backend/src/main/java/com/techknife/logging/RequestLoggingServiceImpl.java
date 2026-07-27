package com.techknife.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service implementation for formatting and emitting structured HTTP request execution logs.
 */
@Slf4j
@Service
public class RequestLoggingServiceImpl implements RequestLoggingService {

    @Value("${logging.request.enabled:true}")
    private boolean enabled;

    @Value("${logging.request.slow-threshold-ms:1000}")
    private long slowThresholdMs;

    @Override
    public void logRequest(RequestLog requestLog) {
        if (!enabled || requestLog == null) {
            return;
        }

        try {
            boolean isSlow = requestLog.getExecutionTimeMs() != null && requestLog.getExecutionTimeMs() >= slowThresholdMs;
            requestLog.setSlowRequest(isSlow);

            String logMsg = formatRequestLog(requestLog);

            if (isSlow) {
                log.warn("[SLOW REQUEST DETECTED] {}", logMsg);
            } else if (requestLog.getStatus() != null && requestLog.getStatus() >= 500) {
                log.error("[HTTP SERVER ERROR] {}", logMsg);
            } else if (requestLog.getStatus() != null && requestLog.getStatus() >= 400) {
                log.warn("[HTTP CLIENT ERROR] {}", logMsg);
            } else {
                log.info("[HTTP REQUEST] {}", logMsg);
            }
        } catch (Exception ex) {
            // Logging failure should never disrupt business logic execution
            log.error("Failed to format or emit HTTP request log entry: {}", ex.getMessage());
        }
    }

    @Async
    @Override
    public void logAsync(RequestLog requestLog) {
        logRequest(requestLog);
    }

    private String formatRequestLog(RequestLog log) {
        StringBuilder sb = new StringBuilder();
        sb.append("corrId=").append(defaultString(log.getCorrelationId(), "N/A"))
          .append(" | method=").append(defaultString(log.getMethod(), "UNKNOWN"))
          .append(" | uri=").append(defaultString(log.getRequestUri(), "/"));

        if (StringUtils.hasText(log.getQueryParams())) {
            sb.append("?").append(log.getQueryParams());
        }

        sb.append(" | status=").append(log.getStatus() != null ? log.getStatus() : 0)
          .append(" | duration=").append(log.getExecutionTimeMs() != null ? log.getExecutionTimeMs() : 0).append("ms")
          .append(" | ip=").append(defaultString(log.getClientIp(), "0.0.0.0"))
          .append(" | user=").append(defaultString(log.getUserId(), "ANONYMOUS"));

        if (StringUtils.hasText(log.getUserRole())) {
            sb.append(" (").append(log.getUserRole()).append(")");
        }

        if (log.getResponseSize() != null && log.getResponseSize() > 0) {
            sb.append(" | size=").append(log.getResponseSize()).append("B");
        }

        if (log.isSlowRequest()) {
            sb.append(" | SLOW_REQUEST (Threshold: ").append(slowThresholdMs).append("ms)");
        }

        return sb.toString();
    }

    private String defaultString(String str, String defaultVal) {
        return StringUtils.hasText(str) ? str : defaultVal;
    }
}
