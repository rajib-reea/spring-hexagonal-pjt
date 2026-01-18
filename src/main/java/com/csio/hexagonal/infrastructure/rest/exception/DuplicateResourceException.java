package com.csio.hexagonal.infrastructure.rest.exception;

import org.springframework.http.HttpStatus;

/**
 * REST layer exception for duplicate resource attempts.
 * Thrown when attempting to create a resource that already exists.
 */
public class DuplicateResourceException extends RestApiException {
    
    public DuplicateResourceException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
