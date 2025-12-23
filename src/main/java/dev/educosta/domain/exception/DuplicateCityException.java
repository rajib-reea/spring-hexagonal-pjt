
package dev.educosta.domain.exception;

public class DuplicateCityException extends RuntimeException {
    public DuplicateCityException(String name) {
        super("City already exists: " + name);
    }
}
