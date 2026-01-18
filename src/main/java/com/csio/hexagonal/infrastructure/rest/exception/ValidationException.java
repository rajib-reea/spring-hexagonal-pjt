package com.csio.hexagonal.infrastructure.rest.exception;

import org.springframework.http.HttpStatus;

/**
 * REST layer exception for validation errors.
 * Thrown when request data fails validation.
 */
public class ValidationException extends RestApiException {
    
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ValidationException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}
