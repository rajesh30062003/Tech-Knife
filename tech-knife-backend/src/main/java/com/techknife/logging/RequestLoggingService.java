package com.techknife.logging;

/**
 * Service interface for processing, formatting, and persisting HTTP request execution log entries.
 */
public interface RequestLoggingService {

    /**
     * Synchronously records formatted HTTP request trace log entry.
     *
     * @param requestLog Captured HTTP request execution metadata
     */
    void logRequest(RequestLog requestLog);

    /**
     * Asynchronously records HTTP request trace log entry to avoid blocking client threads.
     *
     * @param requestLog Captured HTTP request execution metadata
     */
    void logAsync(RequestLog requestLog);
}
