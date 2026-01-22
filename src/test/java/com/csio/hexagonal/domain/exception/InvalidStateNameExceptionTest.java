package com.csio.hexagonal.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidStateNameExceptionTest {

    @Test
    void shouldCreateExceptionWithMessage() {
        // Arrange
        String message = "Invalid state name";

        // Act
        InvalidStateNameException exception = new InvalidStateNameException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithoutMessage() {
        // Act
        InvalidStateNameException exception = new InvalidStateNameException();

        // Assert
        assertNotNull(exception);
    }

    @Test
    void shouldThrowExceptionForNullStateName() {
        // Act & Assert
        InvalidStateNameException exception = assertThrows(InvalidStateNameException.class, () -> {
            InvalidStateNameException.validate(null);
        });

        assertTrue(exception.getMessage().contains("must not be empty"));
    }

    @Test
    void shouldThrowExceptionForBlankStateName() {
        // Act & Assert
        InvalidStateNameException exception = assertThrows(InvalidStateNameException.class, () -> {
            InvalidStateNameException.validate("   ");
        });

        assertTrue(exception.getMessage().contains("must not be empty"));
    }

    @Test
    void shouldThrowExceptionForStateNameWithNumbers() {
        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            InvalidStateNameException.validate("State123");
        });
    }

    @Test
    void shouldThrowExceptionForStateNameWithSpecialCharacters() {
        // Act & Assert
        assertThrows(InvalidCityNameException.class, () -> {
            InvalidStateNameException.validate("State@Name");
        });
    }

    @Test
    void shouldNotThrowExceptionForValidStateName() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            InvalidStateNameException.validate("New York");
        });
    }

    @Test
    void shouldNotThrowExceptionForStateNameWithHyphens() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            InvalidStateNameException.validate("North-Carolina");
        });
    }
}
