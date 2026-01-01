package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.infrastructure.rest.response.wrapper.ErrorResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
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

        ExceptionMetadataRegistry.ExceptionMetadata metadata = ExceptionMetadataRegistry.getMetadata(ex);

        ExceptionDetail detail = new ExceptionDetail(
                exchange.getRequest().getPath().value(),
                metadata.errorTitle(),
                ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName(),
                Instant.now()
        );

        ErrorResponseWrapper wrapper = new ErrorResponseWrapper(metadata.status().value(), detail);

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(metadata.status());

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(wrapper);
        } catch (Exception e) {
            bytes = ("{\"status\":500,\"exception\":{\"message\":\"Internal error\"}}").getBytes();
        }

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
