
package dev.educosta.domain.service;

import dev.educosta.domain.model.City;
import java.util.List;

public interface CityUniquenessChecker {
    void ensureUnique(City city, List<City> existingCities);
}
