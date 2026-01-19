package com.csio.hexagonal.domain.policy.city;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CityPolicyEnforcerTest {

    private CityPolicyEnforcer policyEnforcer;

    @BeforeEach
    void setUp() {
        policyEnforcer = new CityPolicyEnforcer();
    }

    @Test
    void shouldNotThrowExceptionWhenCityIsUnique() {
        // Arrange
        City newCity = new City(CityId.newId(), "New York", new State("NY"));
        City existingCity1 = new City(CityId.newId(), "Los Angeles", new State("CA"));
        City existingCity2 = new City(CityId.newId(), "Chicago", new State("IL"));
        List<City> existingCities = Arrays.asList(existingCity1, existingCity2);

        // Act & Assert
        assertDoesNotThrow(() -> {
            policyEnforcer.ensureUnique(newCity, existingCities);
        });
    }

    @Test
    void shouldThrowExceptionWhenCityNameAlreadyExists() {
        // Arrange
        City newCity = new City(CityId.newId(), "New York", new State("NY"));
        City existingCity = new City(CityId.newId(), "New York", new State("CA"));
        List<City> existingCities = Collections.singletonList(existingCity);

        // Act & Assert
        DuplicateCityException exception = assertThrows(DuplicateCityException.class, () -> {
            policyEnforcer.ensureUnique(newCity, existingCities);
        });

        assertTrue(exception.getMessage().contains("New York"));
    }

    @Test
    void shouldThrowExceptionWhenCityNameExistsCaseInsensitive() {
        // Arrange
        City newCity = new City(CityId.newId(), "new york", new State("NY"));
        City existingCity = new City(CityId.newId(), "New York", new State("NY"));
        List<City> existingCities = Collections.singletonList(existingCity);

        // Act & Assert
        assertThrows(DuplicateCityException.class, () -> {
            policyEnforcer.ensureUnique(newCity, existingCities);
        });
    }

    @Test
    void shouldNotThrowExceptionWhenExistingCitiesIsEmpty() {
        // Arrange
        City newCity = new City(CityId.newId(), "New York", new State("NY"));
        List<City> existingCities = Collections.emptyList();

        // Act & Assert
        assertDoesNotThrow(() -> {
            policyEnforcer.ensureUnique(newCity, existingCities);
        });
    }

    @Test
    void shouldAllowDifferentCitiesWithSimilarNamesButNotExactMatch() {
        // Arrange
        City newCity = new City(CityId.newId(), "New York City", new State("NY"));
        City existingCity = new City(CityId.newId(), "New York", new State("NY"));
        List<City> existingCities = Collections.singletonList(existingCity);

        // Act & Assert
        assertDoesNotThrow(() -> {
            policyEnforcer.ensureUnique(newCity, existingCities);
        });
    }
}
