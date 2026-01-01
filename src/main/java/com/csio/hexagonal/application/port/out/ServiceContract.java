package com.csio.hexagonal.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface ServiceContract<T, R, ID> {

    R save(T entity, String token);

    Optional<T> findByUid(UUID id, String token);

    // Page<T> findAll(Pagination paginationRequest, String token);

    T update(String uid, T entity, String token);

    void deleteByUid(String uid, String token);
}
