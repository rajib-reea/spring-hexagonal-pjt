package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;

import java.util.List;
import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    List<City> findAllWithPagination(int page, int size, String search, String sort, String token);
    List<City> findAllWithFilters(CityFindAllRequest request, String token);

}
