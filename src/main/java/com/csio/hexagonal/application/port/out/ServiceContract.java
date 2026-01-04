package com.csio.hexagonal.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceContract<T, R, ID> {

    R save(T entity, String token);

    Optional<T> findByUid(UUID id, String token);

    List<T> findAll(String token);  // <-- added generic findAll

    T update(String uid, T entity, String token);

    void deleteByUid(String uid, String token);
}
