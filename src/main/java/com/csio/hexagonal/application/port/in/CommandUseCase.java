package com.csio.hexagonal.application.port.in;

import reactor.core.publisher.Mono;

public interface CommandUseCase<T, R> {
    Mono<R> create(T entity, String token);
    // Mono<T> update(String uid, T entity, String token);
    // Mono<Void> deleteByUid(String uid, String token);
}
