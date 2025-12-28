
package com.csio.hexagonal.domain.service;

import com.csio.hexagonal.domain.model.City;
import java.util.List;

public interface CityUniquenessChecker {
    void ensureUnique(City city, List<City> existingCities);
}
