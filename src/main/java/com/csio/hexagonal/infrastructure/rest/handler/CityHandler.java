package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CreateCityUseCase;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import com.csio.hexagonal.infrastructure.rest.mapper.CityRestMapper;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Component;


@Component
public class CityHandler {

    private final CreateCityUseCase useCase;
    private final CityRestMapper mapper;

    public CityHandler(CreateCityUseCase useCase, CityRestMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Operation(
        summary = CitySpec.CREATE_SUMMARY,
        description = CitySpec.CREATE_DESCRIPTION
    )
    public Mono<ServerResponse> createCity(ServerRequest request) {

        return request.bodyToMono(CreateCityRequest.class)
                .map(mapper::toCommand)
                .doOnNext(useCase::create)
                .then(ServerResponse.ok().build());
    }
}
