package com.csio.hexagonal.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Contract<T, R, ID> {

    R save(T entity, String token);

    Optional<T> findByUid(ID id, String token);

    List<T> findAll(String token);

    T update(ID id, T entity, String token);

    void deleteByUid(ID id, String token);
}
