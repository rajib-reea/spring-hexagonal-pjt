package com.csio.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicateCityExceptionTest {

    @Test
    void shouldCreateExceptionWithCityName() {
        // Arrange
        String cityName = "New York";

        // Act
        DuplicateCityException exception = new DuplicateCityException(cityName);

        // Assert
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("City already exists"));
        assertTrue(exception.getMessage().contains(cityName));
    }

    @Test
    void shouldCreateExceptionWithoutMessage() {
        // Act
        DuplicateCityException exception = new DuplicateCityException();

        // Assert
        assertNotNull(exception);
    }

    @Test
    void shouldBeRuntimeException() {
        // Arrange
        DuplicateCityException exception = new DuplicateCityException("Test City");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }
}
