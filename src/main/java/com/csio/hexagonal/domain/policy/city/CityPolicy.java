
package com.csio.hexagonal.domain.policy.city;

import com.csio.hexagonal.domain.model.City;
import java.util.List;

public interface CityPolicy {
    void ensureUnique(City city, List<City> existingCities);
}
