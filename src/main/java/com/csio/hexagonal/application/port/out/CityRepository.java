
package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;
import java.util.List;

public interface CityRepository {
    void save(City city);
    List<City> findAll();
}
