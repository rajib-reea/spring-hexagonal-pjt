package com.csio.hexagonal.infrastructure.store.persistence.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;

public final class CityPersistenceMapper {

    private CityPersistenceMapper() {}

    public static CityEntity toEntity(City city) {
        CityEntity entity = new CityEntity();

        entity.setUid(city.getId().value().toString());
        entity.setName(city.getName());
        entity.setState(city.getState().value());
        entity.setIsActive(city.isActive());

        return entity;
    }

    public static City toDomain(CityEntity entity) {
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
}
