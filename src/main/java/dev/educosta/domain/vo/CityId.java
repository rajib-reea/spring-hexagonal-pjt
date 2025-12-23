
package dev.educosta.domain.vo;

import java.util.UUID;

public record CityId(String value) {
    public static CityId newId() {
        return new CityId(UUID.randomUUID().toString());
    }
}
