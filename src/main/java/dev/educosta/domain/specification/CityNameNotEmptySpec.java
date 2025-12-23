
package dev.educosta.domain.specification;

import dev.educosta.domain.exception.InvalidCityNameException;

public class CityNameNotEmptySpec {
    public void check(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidCityNameException();
        }
    }
}
