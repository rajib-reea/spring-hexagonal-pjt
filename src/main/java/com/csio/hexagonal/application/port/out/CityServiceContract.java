package com.csio.hexagonal.application.port.out;

import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.model.City;
import java.util.UUID;

public interface CityServiceContract extends ServiceContract<City, City, UUID> {
    PageResult<City> findAllWithPagination(int page, int size, String search, String sort, String token);

    PageResult<City> findAllWithFilters(CityFilterQuery request, String token);

}
