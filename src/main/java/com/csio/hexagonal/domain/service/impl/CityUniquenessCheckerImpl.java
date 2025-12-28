
package com.csio.hexagonal.domain.service.impl;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.service.CityUniquenessChecker;

import java.util.List;

public class CityUniquenessCheckerImpl implements CityUniquenessChecker {

    @Override
    public void ensureUnique(City city, List<City> existingCities) {
        boolean exists = existingCities.stream()
                .anyMatch(c -> c.name().equalsIgnoreCase(city.name()));
        if (exists) {
            throw new DuplicateCityException(city.name());
        }
    }
}
