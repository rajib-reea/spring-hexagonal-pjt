
package dev.educosta.domain.service.impl;

import dev.educosta.domain.exception.DuplicateCityException;
import dev.educosta.domain.model.City;
import dev.educosta.domain.service.CityUniquenessChecker;

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
