package com.techknife.backend.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standard generic API response wrapper for unifying REST API outputs.
 *
 * @param <T> Payload data type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates whether the API request was successful.
     */
    private boolean success;

    /**
     * Descriptive outcome message for client applications.
     */
    private String message;

    /**
     * Generic response payload.
     */
    private T data;

    /**
     * Structured error response details if request failed.
     */
    private ApiError error;

    /**
     * UTC Timestamp of response generation.
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Creates a successful API response with data payload and message.
     *
     * @param data response payload
     * @param message outcome message
     * @param <T> payload type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates a successful API response with data payload and default message.
     *
     * @param data response payload
     * @param <T> payload type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    /**
     * Creates a successful API response with message only.
     *
     * @param message outcome message
     * @param <T> payload type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * Creates an error API response with a message and error details object.
     *
     * @param message error summary message
     * @param error ApiError model
     * @param <T> payload type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message, ApiError error) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(error)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an error API response with message only.
     *
     * @param message error summary message
     * @param <T> payload type
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
}
