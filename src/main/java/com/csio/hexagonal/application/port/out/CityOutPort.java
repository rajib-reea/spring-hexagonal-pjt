
package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;
import java.util.List;

public interface CityOutPort extends ServiceContract<City, City, String> {
    List<City> findAll();
}
