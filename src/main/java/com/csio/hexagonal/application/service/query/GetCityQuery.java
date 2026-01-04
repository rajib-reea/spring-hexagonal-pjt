package com.csio.hexagonal.application.service.query;

import java.util.UUID;

/**
 * Read-only query object for fetching a city by UID
 */
public record GetCityQuery(UUID uid) {

    public static GetCityQuery fromString(String uid) {
        return new GetCityQuery(UUID.fromString(uid));
    }
}
