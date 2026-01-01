package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;
import java.util.Map;
import java.util.HashMap;

public final class ExceptionMetadataRegistry {

    private static final Map<Class<? extends Throwable>, ExceptionMetadata> registry = new HashMap<>();

    static {
        registry.put(DuplicateCityException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Duplicate City Error"));
        registry.put(InvalidCityNameException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Validation Error"));
        registry.put(ServerWebInputException.class, new ExceptionMetadata(HttpStatus.BAD_REQUEST, "Invalid Input"));
        // add more exceptions here
    }

    public static ExceptionMetadata getMetadata(Throwable ex) {
        return registry.getOrDefault(ex.getClass(),
                new ExceptionMetadata(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));
    }

    public record ExceptionMetadata(HttpStatus status, String errorTitle) {}
}
