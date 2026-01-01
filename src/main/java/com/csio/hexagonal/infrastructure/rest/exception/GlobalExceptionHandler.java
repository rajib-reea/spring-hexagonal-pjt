package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.infrastructure.rest.response.exception.ExceptionDetail;
import com.csio.hexagonal.infrastructure.rest.response.exception.ErrorResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {

        HttpStatus status;
        String message;
        String errorTitle; // <-- custom "error" field

        // --- Determine HTTP status, message, and custom error ---
        if (ex instanceof DuplicateCityException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
            errorTitle = "Duplicate City Error";
        } else if (ex instanceof InvalidCityNameException) {
            status = HttpStatus.BAD_REQUEST;
            message = (ex.getMessage() != null) ? ex.getMessage() : "Invalid city name";
            errorTitle = "Validation Error";
        } else if (ex instanceof ServerWebInputException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
            errorTitle = "Invalid Input";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getSimpleName();
            errorTitle = "Server Error";
        }

        // --- Build inner exception detail ---
        ExceptionDetail detail = new ExceptionDetail(
                exchange.getRequest().getPath().value(),
                errorTitle,   // use custom error string here
                message,
                Instant.now() // ISO-8601 timestamp
        );

        // --- Wrap in top-level response ---
        ErrorResponseWrapper responseWrapper = new ErrorResponseWrapper(
                status.value(),
                detail
        );

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(responseWrapper);
        } catch (Exception e) {
            bytes = ("{\"status\":500,\"exception\":{\"message\":\"Internal error\"}}").getBytes();
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)
                ));
    }
}
