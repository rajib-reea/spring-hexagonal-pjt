package com.csio.hexagonal.infrastructure.store.persistence.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;

import java.util.UUID;

public final class CityMapper {

    public CityMapper() {}

    public static CityEntity toEntity(City city) {
        CityEntity entity = new CityEntity();

        entity.setUid(String.valueOf(UUID.fromString(city.getId().value().toString())));
        entity.setName(city.getName());
        entity.setState(city.getState().value());
        entity.setIsActive(city.isActive());

        return entity;
    }

    public static City toModel(CityEntity entity) {
        City city = new City(
                CityId.from(entity.getUid()),
                entity.getName(),
                new State(entity.getState())
        );

        if (!Boolean.TRUE.equals(entity.getIsActive())) {
            city.deactivate();
        }

        return city;
    }
    /**
     * Domain model → REST response
     */
    public static CityResponse toResponse(CityEntity city) {
        return new CityResponse(
                city.getUid().toString(), // UUID → String
                city.getIsActive(),
                city.getName(),
                city.getState()
        );
    }
}
