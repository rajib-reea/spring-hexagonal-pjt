package com.csio.hexagonal.infrastructure.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;
import java.util.Map;
import java.util.HashMap;

public final class ExceptionMetadataRegistry {

    private static final Map<Class<? extends Throwable>, ExceptionMetadata> registry = new HashMap<>();

    static {
        // REST layer exceptions (infrastructure)
        registry.put(DuplicateResourceException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Duplicate Resource Error"));
        registry.put(ValidationException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Validation Error"));
        registry.put(RestApiException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "API Error"));
        
        // Framework exceptions
        registry.put(ServerWebInputException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Invalid Input"));
        registry.put(IllegalArgumentException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Invalid Parameter"));
    }

    public static ExceptionMetadata getMetadata(Throwable ex) {
        // Check for RestApiException and its subclasses
        if (ex instanceof RestApiException restApiEx) {
            return new ExceptionMetadata(restApiEx.getStatus(), getErrorTitle(ex));
        }
        
        return registry.getOrDefault(ex.getClass(),
                new ExceptionMetadata(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));
    }
    
    private static String getErrorTitle(Throwable ex) {
        ExceptionMetadata metadata = registry.get(ex.getClass());
        return metadata != null ? metadata.errorTitle() : "API Error";
    }

    public record ExceptionMetadata(HttpStatus status, String errorTitle) {}
}
