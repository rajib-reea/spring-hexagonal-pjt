package com.csio.hexagonal.domain.model;

import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.domain.exception.InvalidStateNameException;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {

    @Test
    void shouldCreateValidCity() {
        // Arrange
        CityId id = CityId.newId();
        String name = "New York";
        State state = new State("NY");

        // Act
        City city = new City(id, name, state);

        // Assert
        assertNotNull(city);
        assertEquals(id, city.getId());
        assertEquals(name, city.getName());
        assertEquals(state, city.getState());
        assertTrue(city.isActive());
    }

    @Test
    void shouldThrowExceptionWhenCityNameIsNull() {
        // Arrange
        CityId id = CityId.newId();
        State state = new State("NY");

        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            new City(id, null, state);
        });
    }

    @Test
    void shouldThrowExceptionWhenCityNameIsBlank() {
        // Arrange
        CityId id = CityId.newId();
        State state = new State("NY");

        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            new City(id, "   ", state);
        });
    }

    @Test
    void shouldThrowExceptionWhenCityNameContainsInvalidCharacters() {
        // Arrange
        CityId id = CityId.newId();
        State state = new State("NY");

        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            new City(id, "New York123", state);
        });
    }

    @Test
    void shouldThrowExceptionWhenStateNameIsInvalid() {
        // Arrange
        CityId id = CityId.newId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new State("   ");
        });
    }

    @Test
    void shouldThrowExceptionWhenCityIdIsNull() {
        // Arrange
        State state = new State("NY");

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            new City(null, "New York", state);
        });
    }

    @Test
    void shouldActivateCity() {
        // Arrange
        City city = new City(CityId.newId(), "New York", new State("NY"));
        city.deactivate();

        // Act
        city.activate();

        // Assert
        assertTrue(city.isActive());
    }

    @Test
    void shouldDeactivateCity() {
        // Arrange
        City city = new City(CityId.newId(), "New York", new State("NY"));

        // Act
        city.deactivate();

        // Assert
        assertFalse(city.isActive());
    }

    @Test
    void shouldBeEqualWhenSameId() {
        // Arrange
        CityId id = CityId.newId();
        City city1 = new City(id, "New York", new State("NY"));
        City city2 = new City(id, "Different Name", new State("CA"));

        // Act & Assert
        assertEquals(city1, city2);
        assertEquals(city1.hashCode(), city2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentId() {
        // Arrange
        City city1 = new City(CityId.newId(), "New York", new State("NY"));
        City city2 = new City(CityId.newId(), "New York", new State("NY"));

        // Act & Assert
        assertNotEquals(city1, city2);
    }

    @Test
    void shouldAllowValidCityNameWithHyphens() {
        // Arrange
        CityId id = CityId.newId();
        State state = new State("PA");

        // Act
        City city = new City(id, "Wilkes-Barre", state);

        // Assert
        assertNotNull(city);
        assertEquals("Wilkes-Barre", city.getName());
    }

    @Test
    void shouldAllowValidCityNameWithSpaces() {
        // Arrange
        CityId id = CityId.newId();
        State state = new State("CA");

        // Act
        City city = new City(id, "San Francisco", state);

        // Assert
        assertNotNull(city);
        assertEquals("San Francisco", city.getName());
    }

    @Test
    void shouldHaveToStringMethod() {
        // Arrange
        CityId id = CityId.newId();
        City city = new City(id, "New York", new State("NY"));

        // Act
        String toString = city.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("City{"));
        assertTrue(toString.contains("New York"));
        assertTrue(toString.contains("NY"));
    }
}
