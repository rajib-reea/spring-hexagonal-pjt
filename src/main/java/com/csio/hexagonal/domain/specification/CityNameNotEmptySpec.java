
package com.csio.hexagonal.domain.specification;

import com.csio.hexagonal.domain.exception.InvalidCityNameException;

public class CityNameNotEmptySpec {
    public void check(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidCityNameException();
        }
    }
}
