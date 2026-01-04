package com.csio.hexagonal.application.port.in;

import reactor.core.publisher.Mono;

public interface QueryUseCase<Q, R> {

    Mono<R> query(Q query, String token);
}
