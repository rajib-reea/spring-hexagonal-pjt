package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;

import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    // You can add city-specific methods here if needed
    // For example:
    // List<City> findActiveCities(String token);
}
