package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message;

        if (ex instanceof DuplicateCityException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage(); // already set in constructor
        } else if (ex instanceof InvalidCityNameException) {
            status = HttpStatus.BAD_REQUEST;
            // Provide a default message if exception has no message
            message = (ex.getMessage() != null) ? ex.getMessage() : "Invalid city name";
        } else if (ex instanceof ServerWebInputException) {
            status = HttpStatus.BAD_REQUEST;
            message = ex.getMessage();
        } else {
            message = (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getSimpleName();
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", exchange.getRequest().getPath().value());

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"message\":\"Internal error\"}").getBytes();
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)
                ));
    }
}
