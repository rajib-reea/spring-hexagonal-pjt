package com.csio.hexagonal.infrastructure.rest.response.city;

import com.csio.hexagonal.infrastructure.rest.response.ResponseInclusion;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CityResponse(
        String uid,
        Boolean isActive,
        String name,
        String state
) implements ResponseInclusion {}
