package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    PageResponseWrapper<CityResponse> findAllWithPagination(int page, int size, String search, String sort, String token);

    PageResponseWrapper<CityResponse> findAllWithFilters(CityFindAllRequest request, String token);

}
