package com.csio.hexagonal.application.port.in;

import java.util.Optional;

public interface QueryUseCase<T> {
    Optional<T> getByUid(String uid, String token);
}
