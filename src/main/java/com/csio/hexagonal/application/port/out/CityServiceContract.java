package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;
import java.util.List;
import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    List<City> findAllWithPagination(int page, int size, String search, String sort, String token);
}
