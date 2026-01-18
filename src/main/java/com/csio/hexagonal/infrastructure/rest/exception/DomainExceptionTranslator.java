package com.csio.hexagonal.infrastructure.rest.exception;

import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.exception.InvalidCityNameException;
import com.csio.hexagonal.domain.exception.InvalidStateNameException;

/**
 * Translates domain exceptions to REST layer exceptions at the adapter boundary.
 * This maintains proper separation of concerns in hexagonal architecture.
 */
public final class DomainExceptionTranslator {

    private DomainExceptionTranslator() {
        // Utility class - prevent instantiation
    }

    /**
     * Translates a domain exception to a REST exception.
     * 
     * @param domainException the domain exception to translate
     * @return the corresponding REST exception
     */
    public static RestApiException translate(Throwable domainException) {
        if (domainException instanceof DuplicateCityException) {
            return new DuplicateResourceException(domainException.getMessage(), domainException);
        } else if (domainException instanceof InvalidCityNameException) {
            return new ValidationException(domainException.getMessage(), domainException);
        } else if (domainException instanceof InvalidStateNameException) {
            return new ValidationException(domainException.getMessage(), domainException);
        }
        
        // If not a known domain exception, return as-is or wrap in generic RestApiException
        if (domainException instanceof RestApiException) {
            return (RestApiException) domainException;
        }
        
        // Unknown exception - let it propagate to global handler
        if (domainException instanceof RuntimeException) {
            throw (RuntimeException) domainException;
        }
        throw new RuntimeException(domainException);
    }
}
