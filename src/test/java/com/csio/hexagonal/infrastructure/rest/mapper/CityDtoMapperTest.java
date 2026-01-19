package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CityDtoMapperTest {

    @Test
    void shouldMapCityToResponse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);
        City city = new City(cityId, "New York", new State("NY"));

        // Act
        CityResponse response = CityDtoMapper.toResponse(city);

        // Assert
        assertNotNull(response);
        assertEquals(uuid.toString(), response.uid());
        assertEquals(true, response.isActive());
        assertEquals("New York", response.name());
        assertEquals("NY", response.state());
    }

    @Test
    void shouldMapInactiveCityToResponse() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);
        City city = new City(cityId, "Los Angeles", new State("CA"));
        city.deactivate();

        // Act
        CityResponse response = CityDtoMapper.toResponse(city);

        // Assert
        assertNotNull(response);
        assertEquals(false, response.isActive());
    }

    @Test
    void shouldMapPageResultToPageResponseWrapper() {
        // Arrange
        City city1 = new City(CityId.newId(), "New York", new State("NY"));
        City city2 = new City(CityId.newId(), "Los Angeles", new State("CA"));
        
        CityResponse response1 = CityDtoMapper.toResponse(city1);
        CityResponse response2 = CityDtoMapper.toResponse(city2);
        List<CityResponse> content = Arrays.asList(response1, response2);
        
        PageResult<CityResponse> pageResult = PageResult.of(content, 1, 10, 2, 1);

        // Act
        PageResponseWrapper<CityResponse> wrapper = CityDtoMapper.toPageResponseWrapper(pageResult);

        // Assert
        assertNotNull(wrapper);
        assertEquals(200, wrapper.status());
        assertEquals(1, wrapper.meta().page());
        assertEquals(10, wrapper.meta().size());
        assertEquals(0L, wrapper.meta().offset());
        assertEquals(2L, wrapper.meta().totalElements());
        assertEquals(1, wrapper.meta().totalPages());
        assertEquals(2, wrapper.data().size());
    }

    @Test
    void shouldMapEmptyPageResultToPageResponseWrapper() {
        // Arrange
        List<CityResponse> content = List.of();
        PageResult<CityResponse> pageResult = PageResult.of(content, 1, 10, 0, 0);

        // Act
        PageResponseWrapper<CityResponse> wrapper = CityDtoMapper.toPageResponseWrapper(pageResult);

        // Assert
        assertNotNull(wrapper);
        assertEquals(200, wrapper.status());
        assertEquals(0L, wrapper.meta().totalElements());
        assertEquals(0, wrapper.meta().totalPages());
        assertTrue(wrapper.data().isEmpty());
    }

    @Test
    void shouldCalculateCorrectOffsetForPage2() {
        // Arrange
        List<CityResponse> content = List.of();
        PageResult<CityResponse> pageResult = PageResult.of(content, 2, 10, 15, 2);

        // Act
        PageResponseWrapper<CityResponse> wrapper = CityDtoMapper.toPageResponseWrapper(pageResult);

        // Assert
        assertEquals(10L, wrapper.meta().offset()); // (2-1) * 10 = 10
    }

    @Test
    void shouldCalculateCorrectOffsetForPage3WithSize5() {
        // Arrange
        List<CityResponse> content = List.of();
        PageResult<CityResponse> pageResult = PageResult.of(content, 3, 5, 15, 3);

        // Act
        PageResponseWrapper<CityResponse> wrapper = CityDtoMapper.toPageResponseWrapper(pageResult);

        // Assert
        assertEquals(10L, wrapper.meta().offset()); // (3-1) * 5 = 10
    }
}
