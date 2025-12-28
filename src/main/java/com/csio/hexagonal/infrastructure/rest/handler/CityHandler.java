package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.mapper.CityRestMapper;
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

    private final CommandUseCase<CreateCityCommand, CityResponse> cityCommandUseCase;
    private final CityRestMapper mapper;

    public CityHandler(
            CommandUseCase<CreateCityCommand, CityResponse> cityCommandUseCase,
            CityRestMapper mapper
    ) {
        this.cityCommandUseCase = cityCommandUseCase;
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
                .map(mapper::toCommand)
                .map(command -> cityCommandUseCase.create(command, token))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }
}
