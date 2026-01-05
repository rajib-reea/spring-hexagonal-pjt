package com.csio.hexagonal.infrastructure.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a city")
public record CityCreateRequest(

    @NotBlank(message = "City name is required")
    @Size(max = 100, message = "City name must not exceed 100 characters")
    @Schema(
        description = "Name of the city",
        example = "Dhaka"
    )
    String name,

    @NotBlank(message = "State name is required")
    @Size(max = 100, message = "State name must not exceed 100 characters")
    @Schema(
        description = "State or province of the city",
        example = "Dhaka Division"
    )
    String state
) {}
