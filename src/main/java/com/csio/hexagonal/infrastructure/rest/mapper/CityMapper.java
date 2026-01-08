package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.rest.request.CityCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    /**
     * REST request → Domain model
     */
    public City toModel(CityCreateRequest request) {
        return new City(
                CityId.newId(),
                request.name(),
                new State(request.state())
        );
    }
}
