package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.application.dto.CityQueryRequest;
import com.csio.hexagonal.application.dto.PageResult;
import com.csio.hexagonal.domain.model.City;
import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    PageResult<City> findAllWithPagination(int page, int size, String search, String sort, String token);

    PageResult<City> findAllWithFilters(CityQueryRequest request, String token);

}
