package com.techknife.backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Model representing structured error details for API failure responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    /**
     * Application error code identifier (e.g., VALIDATION_ERROR, ENTITY_NOT_FOUND).
     */
    private String code;

    /**
     * Detailed error message or failure summary.
     */
    private String details;

    /**
     * Request URI path where error occurred.
     */
    private String path;

    /**
     * Field-level validation error details (field name to message mapping).
     */
    private Map<String, String> fieldErrors;

    /**
     * UTC timestamp when error occurred.
     */
    @Builder.Default
    private Instant timestamp = Instant.now();
}
