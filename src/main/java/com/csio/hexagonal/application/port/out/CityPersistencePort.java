
package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;

import java.util.List;
import java.util.Optional;

public interface CityPersistencePort extends ServiceContract<City, City, String> {
    List<City> findAll();
    Optional<City> findByUid(String uid, String token);
}
