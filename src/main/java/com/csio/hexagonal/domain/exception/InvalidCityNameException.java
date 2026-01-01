
// package com.csio.hexagonal.domain.exception;

// public class InvalidCityNameException extends RuntimeException {}
// // public class InvalidCityNameException extends RuntimeException {
// //     public InvalidCityNameException(String message) {
// //         super(message);
// //     }
// // }

package com.csio.hexagonal.domain.exception;

public class InvalidCityNameException extends RuntimeException {
    public InvalidCityNameException() {
        super();
    }

    public InvalidCityNameException(String message) {
        super(message);
    }
}
