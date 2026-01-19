package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.domain.exception.InvalidStateNameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTranslatorTest {

    @Test
    void shouldTranslateDuplicateCityExceptionToDuplicateResourceException() {
        // Arrange
        DuplicateCityException domainException = new DuplicateCityException("New York");

        // Act
        RestApiException result = DomainExceptionTranslator.translate(domainException);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof DuplicateResourceException);
        assertTrue(result.getMessage().contains("New York"));
    }

    @Test
    void shouldTranslateInvalidCityNameExceptionToValidationException() {
        // Arrange
        InvalidCityNameException domainException = new InvalidCityNameException("Invalid city name");

        // Act
        RestApiException result = DomainExceptionTranslator.translate(domainException);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof ValidationException);
        assertTrue(result.getMessage().contains("Invalid city name"));
    }

    @Test
    void shouldTranslateInvalidStateNameExceptionToValidationException() {
        // Arrange
        InvalidStateNameException domainException = new InvalidStateNameException("Invalid state name");

        // Act
        RestApiException result = DomainExceptionTranslator.translate(domainException);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof ValidationException);
        assertTrue(result.getMessage().contains("Invalid state name"));
    }

    @Test
    void shouldReturnRestApiExceptionAsIs() {
        // Arrange
        ValidationException restException = new ValidationException("Already a REST exception");

        // Act
        RestApiException result = DomainExceptionTranslator.translate(restException);

        // Assert
        assertSame(restException, result);
    }

    @Test
    void shouldThrowRuntimeExceptionForUnknownException() {
        // Arrange
        RuntimeException unknownException = new RuntimeException("Unknown exception");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            DomainExceptionTranslator.translate(unknownException);
        });
    }

    @Test
    void shouldWrapCheckedExceptionInRuntimeException() {
        // Arrange
        Exception checkedException = new Exception("Checked exception");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            DomainExceptionTranslator.translate(checkedException);
        });
    }
}
