package com.techknife.backend.exception;

import com.techknife.backend.response.ApiError;
import com.techknife.backend.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler providing centralized error handling across REST controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle HttpMessageNotReadableException for malformed JSON or invalid enum values.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("HTTP message not readable (JSON parse error): {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("INVALID_JSON")
                .details(ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid Request Payload Format", error));
    }

    /**
     * Handle Jakarta Validation exceptions.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        log.error("Validation error encountered: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("VALIDATION_ERROR")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation Failed", error));
    }

    /**
     * Handle Spring MethodArgumentNotValidException for @Valid request payloads.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Method argument validation failed: {}", ex.getMessage());
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            if (err instanceof FieldError fieldError) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                fieldErrors.put(err.getObjectName(), err.getDefaultMessage());
            }
        });

        ApiError error = ApiError.builder()
                .code("INVALID_INPUT")
                .details("Input validation failed for one or more fields")
                .fieldErrors(fieldErrors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation Failed", error));
    }

    /**
     * Handle Spring Security AccessDeniedException for unauthorized role access.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("ACCESS_DENIED")
                .details("You do not have permission to access this resource")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Access Denied", error));
    }

    /**
     * Handle Spring Security AuthenticationException for invalid or missing credentials.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        log.error("Authentication failed: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("UNAUTHORIZED")
                .details(ex.getMessage() != null ? ex.getMessage() : "Authentication credentials missing or invalid")
                .path(request.getRequestURI())
                .build();
        String message = ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "Invalid email or password";
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(message, error));
    }

    /**
     * Handle EntityNotFoundException, ResourceNotFoundException, and UsernameNotFoundException.
     */
    @ExceptionHandler({EntityNotFoundException.class, ResourceNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Entity not found: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("ENTITY_NOT_FOUND")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource Not Found", error));
    }

    /**
     * Handle IllegalArgumentException for invalid parameter input.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Illegal argument exception: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("BAD_REQUEST")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), error));
    }

    /**
     * Handle MongoDB DuplicateKeyException for duplicate unique keys (projectCode, projectId, etc.).
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKeyException(
            DuplicateKeyException ex, HttpServletRequest request) {
        log.error("Duplicate key conflict encountered: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("DUPLICATE_KEY")
                .details("A resource with the specified unique key already exists")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Resource Conflict: Duplicate key error", error));
    }

    /**
     * Handle HttpRequestMethodNotSupportedException for unsupported HTTP methods.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("HTTP method not supported: {} for path '{}'", ex.getMethod(), request.getRequestURI());
        ApiError error = ApiError.builder()
                .code("METHOD_NOT_ALLOWED")
                .details(String.format("HTTP Method '%s' is not supported for this endpoint. Supported methods: %s",
                        ex.getMethod(), ex.getSupportedHttpMethods()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error("Method Not Allowed", error));
    }

    /**
     * Handle UnauthorizedException.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        log.error("Unauthorized request: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("UNAUTHORIZED")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage(), error));
    }

    /**
     * Handle BadRequestException.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        ApiError error = ApiError.builder()
                .code("BAD_REQUEST")
                .details(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage(), error));
    }

    /**
     * Handle unhandled RuntimeExceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        log.error("Runtime exception encountered: ", ex);
        ApiError error = ApiError.builder()
                .code("RUNTIME_ERROR")
                .details(ex.getClass().getName() + ": " + (ex.getMessage() != null ? ex.getMessage() : ex.toString()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Application Error: " + ex.getMessage(), error));
    }

    /**
     * Fallback handler for all general unhandled Exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception encountered: ", ex);
        ApiError error = ApiError.builder()
                .code("INTERNAL_SERVER_ERROR")
                .details(ex.getClass().getName() + ": " + (ex.getMessage() != null ? ex.getMessage() : ex.toString()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal Server Error: " + ex.getMessage(), error));
    }
}
