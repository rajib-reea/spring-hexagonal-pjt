package com.csio.hexagonal.infrastructure.rest.exception;

import org.springframework.http.HttpStatus;

/**
 * Base exception for REST API layer.
 * This exception represents HTTP-specific errors in the REST adapter.
 */
public class RestApiException extends RuntimeException {
    private final HttpStatus status;

    public RestApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public RestApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
