// package com.csio.hexagonal.domain.exception;

// public class InvalidCityNameException extends RuntimeException {
//     public InvalidCityNameException() {
//         super();
//     }

//     public InvalidCityNameException(String message) {
//         super(message);
//     }
// }
package com.csio.hexagonal.domain.exception;

public class InvalidCityNameException extends RuntimeException {
    public InvalidCityNameException() {
        super();
    }

    public InvalidCityNameException(String message) {
        super(message);
    }

    // === Validator rule for city name ===
    public static void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidCityNameException("City name must not be empty");
        }

        // === Rule: only allow letters, spaces, and dashes ===
        if (!name.matches("[a-zA-Z\\s\\-]+")) {
            throw new InvalidCityNameException("City name contains invalid characters");
        }
    }
}
