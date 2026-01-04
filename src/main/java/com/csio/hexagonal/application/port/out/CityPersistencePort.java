package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;

public interface CityPersistencePort extends ServiceContract<City, City, String> {
    // You can add city-specific methods here if needed
    // For example:
    // List<City> findActiveCities(String token);
}
