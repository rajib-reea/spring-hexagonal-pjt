package com.csio.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCityNameExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String message = "Invalid city name";

        // Act
        InvalidCityNameException exception = new InvalidCityNameException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithoutMessage() {
        // Act
        InvalidCityNameException exception = new InvalidCityNameException();

        // Assert
        assertNotNull(exception);
    }

    @Test
    void shouldThrowExceptionForNullCityName() {
        // Act & Assert
        InvalidCityNameException exception = assertThrows(InvalidCityNameException.class, () -> {
            InvalidCityNameException.validate(null);
        });

        assertTrue(exception.getMessage().contains("must not be empty"));
    }

    @Test
    void shouldThrowExceptionForBlankCityName() {
        // Act & Assert
        InvalidCityNameException exception = assertThrows(InvalidCityNameException.class, () -> {
            InvalidCityNameException.validate("   ");
        });

        assertTrue(exception.getMessage().contains("must not be empty"));
    }

    @Test
    void shouldThrowExceptionForCityNameWithNumbers() {
        // Act & Assert
        InvalidCityNameException exception = assertThrows(InvalidCityNameException.class, () -> {
            InvalidCityNameException.validate("City123");
        });

        assertTrue(exception.getMessage().contains("invalid characters"));
    }

    @Test
    void shouldThrowExceptionForCityNameWithSpecialCharacters() {
        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            InvalidCityNameException.validate("City@Name");
        });
    }

    @Test
    void shouldNotThrowExceptionForValidCityName() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            InvalidCityNameException.validate("New York");
        });
    }

    @Test
    void shouldNotThrowExceptionForCityNameWithHyphens() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            InvalidCityNameException.validate("Wilkes-Barre");
        });
    }

    @Test
    void shouldNotThrowExceptionForCityNameWithSpaces() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            InvalidCityNameException.validate("San Francisco");
        });
    }
}
