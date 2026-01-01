package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityPersistencePort extends ServiceContract<City, City, String> {
    List<City> findAll();

    // Use UUID for entity ID
    Optional<City> findByUid(UUID uid, String token);
}
