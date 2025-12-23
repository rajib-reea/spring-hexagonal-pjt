
package dev.educosta.application.port.out;

import dev.educosta.domain.model.City;
import java.util.List;

public interface CityRepository {
    void save(City city);
    List<City> findAll();
}
