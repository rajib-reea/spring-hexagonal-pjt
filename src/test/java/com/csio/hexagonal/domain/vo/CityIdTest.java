package com.csio.hexagonal.domain.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CityIdTest {

    @Test
    void shouldCreateNewCityId() {
        // Act
        CityId cityId = CityId.newId();

        // Assert
        assertNotNull(cityId);
        assertNotNull(cityId.value());
    }

    @Test
    void shouldCreateCityIdFromString() {
        // Arrange
        String uuidString = "123e4567-e89b-12d3-a456-426614174000";

        // Act
        CityId cityId = CityId.from(uuidString);

        // Assert
        assertNotNull(cityId);
        assertEquals(uuidString, cityId.value().toString());
    }

    @Test
    void shouldThrowExceptionForInvalidUuidString() {
        // Arrange
        String invalidUuid = "invalid-uuid";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            CityId.from(invalidUuid);
        });
    }

    @Test
    void shouldHaveToStringMethod() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId = new CityId(uuid);

        // Act
        String result = cityId.toString();

        // Assert
        assertEquals(uuid.toString(), result);
    }

    @Test
    void shouldBeEqualWhenSameUuid() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        CityId cityId1 = new CityId(uuid);
        CityId cityId2 = new CityId(uuid);

        // Act & Assert
        assertEquals(cityId1, cityId2);
        assertEquals(cityId1.hashCode(), cityId2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentUuid() {
        // Arrange
        CityId cityId1 = CityId.newId();
        CityId cityId2 = CityId.newId();

        // Act & Assert
        assertNotEquals(cityId1, cityId2);
    }
}
