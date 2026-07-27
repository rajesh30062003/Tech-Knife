package com.techknife.logging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Data model encapsulating captured HTTP request and response execution metadata for audit and tracing logs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {

    private Instant timestamp;
    private String correlationId;
    private String method;
    private String requestUri;
    private String queryParams;
    private String clientIp;
    private String userId;
    private String userRole;
    private Integer status;
    private Long executionTimeMs;
    private Long responseSize;
    private boolean slowRequest;
    private String userAgent;
}
