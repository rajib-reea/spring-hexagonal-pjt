package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.mapper.CityMapper;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CityHandler {

    private final CommandUseCase<City, CityResponse> commandUseCase;
    private final CityMapper mapper;
     private final String entityName = "City";


    public CityHandler(
            CommandUseCase<City, CityResponse> commandUseCase,
            CityMapper mapper
    ) {
        this.commandUseCase = commandUseCase;
        this.mapper = mapper;
    }

    @Operation(
        summary = CitySpec.CREATE_SUMMARY,
        description = CitySpec.CREATE_DESCRIPTION
    )
    public Mono<ServerResponse> createCity(ServerRequest request) {

        String token = request.headers()
                              .firstHeader("Authorization");

        return request.bodyToMono(CreateCityRequest.class)
                .map(mapper::toModel)
                .map(model -> commandUseCase.create(model, token))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }
}
