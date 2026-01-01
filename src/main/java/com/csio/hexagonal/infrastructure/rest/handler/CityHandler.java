package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.mapper.ResponseMapper;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.concurrent.Executor;
import org.springframework.http.MediaType;

@Component
public class CityHandler {

    private static final Logger log = LoggerFactory.getLogger(CityHandler.class);

    private final CommandUseCase<CreateCityCommand, CityResponse> commandUseCase;
    private final QueryUseCase<CityResponse> queryUseCase;
    private final Executor virtualExecutor;

    public CityHandler(
            CommandUseCase<CreateCityCommand, CityResponse> commandUseCase,
            QueryUseCase<CityResponse> queryUseCase,
            @Qualifier("virtualExecutor") Executor virtualExecutor
    ) {
        this.commandUseCase = commandUseCase;
        this.queryUseCase = queryUseCase;
        this.virtualExecutor = virtualExecutor;
    }

    @Operation(
            summary = CitySpec.CREATE_SUMMARY,
            description = CitySpec.CREATE_DESCRIPTION
    )
    @RequestBody(
            description = CitySpec.CREATE_DESCRIPTION,
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CreateCityRequest.class),
                    examples = @ExampleObject(
                            name = CitySpec.CREATE_EXAMPLE_NAME,
                            value = CitySpec.CREATE_EXAMPLE_VALUE,
                            description = CitySpec.CREATE_EXAMPLE_DESCRIPTION
                    )
            )
    )
    public Mono<ServerResponse> createCity(ServerRequest request) {

        String token = request.headers().firstHeader("Authorization");

        return request.bodyToMono(CreateCityRequest.class)
                .doOnNext(req -> log.info("Received CreateCityRequest: {}", req))
                .map(req -> new CreateCityCommand(req.name(), req.state()))
                .doOnNext(cmd -> log.info("Mapped to CreateCityCommand: {}", cmd))
                // Call service on virtualExecutor
                .flatMap(cmd -> commandUseCase.create(cmd, token)
                        .subscribeOn(Schedulers.fromExecutor(virtualExecutor)))
                .doOnNext(res -> log.info("Service returned response: {}", res))
                // Wrap response using ResponseMapper
                .map(ResponseMapper::success)
                .flatMap(wrapper -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(wrapper)
                );
    }

    @Operation(
            summary = "Get city by UID",
            description = "Retrieve a city by its unique identifier"
    )
    public Mono<ServerResponse> getCity(ServerRequest request) {
        String uid = request.pathVariable("uid");
        String token = request.headers().firstHeader("Authorization");

        log.info("Received request to get city with uid: {}", uid);

        return Mono.fromCallable(() -> queryUseCase.getByUid(uid, token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(optionalCity -> optionalCity
                        .map(city -> {
                            log.info("City found: {}", city);
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(ResponseMapper.success(city));
                        })
                        .orElseGet(() -> {
                            log.warn("City not found with uid: {}", uid);
                            return ServerResponse.notFound().build();
                        }));
    }
}
