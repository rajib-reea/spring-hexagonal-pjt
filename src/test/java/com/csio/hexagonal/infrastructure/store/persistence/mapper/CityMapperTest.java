package com.csio.hexagonal.infrastructure.store.persistence.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.store.persistence.entity.CityEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CityMapperTest {

    @Test
    void shouldMapCityToEntity() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);
        City city = new City(cityId, "New York", new State("NY"));

        // Act
        CityEntity entity = CityMapper.toEntity(city);

        // Assert
        assertNotNull(entity);
        assertEquals(uuid.toString(), entity.getUid());
        assertEquals("New York", entity.getName());
        assertEquals("NY", entity.getState());
        assertTrue(entity.getIsActive());
    }

    @Test
    void shouldMapInactiveCityToEntity() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);
        City city = new City(cityId, "Los Angeles", new State("CA"));
        city.deactivate();

        // Act
        CityEntity entity = CityMapper.toEntity(city);

        // Assert
        assertNotNull(entity);
        assertEquals(uuid.toString(), entity.getUid());
        assertFalse(entity.getIsActive());
    }

    @Test
    void shouldMapEntityToCity() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityEntity entity = new CityEntity();
        entity.setUid(uuid.toString());
        entity.setName("Chicago");
        entity.setState("IL");
        entity.setIsActive(true);

        // Act
        City city = CityMapper.toModel(entity);

        // Assert
        assertNotNull(city);
        assertEquals(uuid, city.getId().value());
        assertEquals("Chicago", city.getName());
        assertEquals("IL", city.getState().value());
        assertTrue(city.isActive());
    }

    @Test
    void shouldMapInactiveEntityToCity() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityEntity entity = new CityEntity();
        entity.setUid(uuid.toString());
        entity.setName("Houston");
        entity.setState("TX");
        entity.setIsActive(false);

        // Act
        City city = CityMapper.toModel(entity);

        // Assert
        assertNotNull(city);
        assertFalse(city.isActive());
    }

    @Test
    void shouldHandleNullIsActiveAsInactive() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityEntity entity = new CityEntity();
        entity.setUid(uuid.toString());
        entity.setName("Phoenix");
        entity.setState("AZ");
        entity.setIsActive(null);

        // Act
        City city = CityMapper.toModel(entity);

        // Assert
        assertNotNull(city);
        assertFalse(city.isActive());
    }

    @Test
    void shouldRoundTripCityToEntityAndBack() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);
        City originalCity = new City(cityId, "San Francisco", new State("CA"));

        // Act
        CityEntity entity = CityMapper.toEntity(originalCity);
        City mappedCity = CityMapper.toModel(entity);

        // Assert
        assertEquals(originalCity.getId(), mappedCity.getId());
        assertEquals(originalCity.getName(), mappedCity.getName());
        assertEquals(originalCity.getState(), mappedCity.getState());
        assertEquals(originalCity.isActive(), mappedCity.isActive());
    }
}
