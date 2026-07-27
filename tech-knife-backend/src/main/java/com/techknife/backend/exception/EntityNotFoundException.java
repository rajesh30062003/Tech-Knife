package com.techknife.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested entity record cannot be found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs EntityNotFoundException with custom message.
     *
     * @param message failure message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs EntityNotFoundException with formatted entity name and identifier.
     *
     * @param entityName name of entity
     * @param identifier entity key or ID
     */
    public EntityNotFoundException(String entityName, Object identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier));
    }
}
