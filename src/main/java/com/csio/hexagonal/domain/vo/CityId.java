package com.csio.hexagonal.domain.vo;

import java.util.UUID;

public record CityId(UUID value) {

    public static CityId newId() {
        return new CityId(UUID.randomUUID());
    }

    public static CityId from(String value) {
        return new CityId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
