package com.csio.hexagonal.application.port.in;

import java.util.Optional;

public interface CommandUseCase<T, R> {
    R create(T entity, String token);
    // T update(String uid, T entity, String token);
    // void deleteByUid(String uid, String token);
}
