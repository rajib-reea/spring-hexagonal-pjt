
package com.csio.hexagonal.domain.exception;

public class DuplicateCityException extends RuntimeException {
     public DuplicateCityException() {
        super();
    }

    public DuplicateCityException(String name) {
        super("City already exists: " + name);
    }
}
