package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.service.command.CreateCityCommand;
import com.csio.hexagonal.application.service.query.GetAllCityQuery;
import com.csio.hexagonal.application.service.query.GetCityQuery;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.helper.ResponseHelper;
import com.csio.hexagonal.infrastructure.rest.request.CityCreateRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.response.wrapper.PageResponseWrapper;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

import java.util.List;
import java.util.concurrent.Executor;

import org.springframework.http.MediaType;

@Component
public class CityHandler {

    private static final Logger log = LoggerFactory.getLogger(CityHandler.class);

    private final CommandUseCase<CreateCityCommand, CityResponse> commandUseCase;
    private final QueryUseCase<GetCityQuery, CityResponse> getCityUseCase;
    private final QueryUseCase<CityFindAllRequest, PageResponseWrapper<CityResponse>> getAllCityUseCase;
    private final Executor virtualExecutor;

    public CityHandler(
            CommandUseCase<CreateCityCommand, CityResponse> commandUseCase,
            QueryUseCase<GetCityQuery, CityResponse> getCityUseCase,
            QueryUseCase<CityFindAllRequest, PageResponseWrapper<CityResponse>> getAllCityUseCase,
            @Qualifier("virtualExecutor") Executor virtualExecutor
    ) {
        this.commandUseCase = commandUseCase;
        this.getCityUseCase = getCityUseCase;
        this.getAllCityUseCase = getAllCityUseCase;
        this.virtualExecutor = virtualExecutor;
    }

    /* ================= CREATE CITY ================= */
    @Operation(
            summary = CitySpec.CREATE_SUMMARY,
            description = CitySpec.CREATE_DESCRIPTION
    )
    @RequestBody(
            description = CitySpec.CREATE_DESCRIPTION,
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CityCreateRequest.class),
                    examples = @ExampleObject(
                            name = CitySpec.CREATE_EXAMPLE_NAME,
                            value = CitySpec.CREATE_EXAMPLE_VALUE,
                            description = CitySpec.CREATE_EXAMPLE_DESCRIPTION
                    )
            )
    )
    public Mono<ServerResponse> createCity(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");

        return request.bodyToMono(CityCreateRequest.class)
                .doOnNext(req -> log.info("Received CreateCityRequest: {}", req))
                .map(req -> new CreateCityCommand(req.name(), req.state()))
                .doOnNext(cmd -> log.info("Mapped to CreateCityCommand: {}", cmd))
                .flatMap(cmd -> commandUseCase.create(cmd, token)
                        .subscribeOn(Schedulers.fromExecutor(virtualExecutor)))
                .doOnNext(res -> log.info("Service returned response: {}", res))
                .map(ResponseHelper::success)
                .flatMap(wrapper -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(wrapper));
    }

    /* ================= GET CITY ================= */
    @Operation(
            summary = CitySpec.GET_SUMMARY,
            description = CitySpec.GET_DESCRIPTION,
            parameters = {
                    @Parameter(
                            name = "uid",
                            in = ParameterIn.PATH,
                            required = true,
                            description = CitySpec.PARAMETER_DESCRIPTION
                    )
            }
    )
    public Mono<ServerResponse> getCity(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");
        String uidStr = request.pathVariable("uid");

        log.info("Received getCity request for uid={}", uidStr);

        GetCityQuery query = GetCityQuery.fromString(uidStr);

        return getCityUseCase.query(query, token)
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .map(ResponseHelper::success)
                .flatMap(wrapper -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(wrapper));
    }

    /* ================= FIND ALL CITIES ================= */

    @Operation(
            summary = CitySpec.GET_ALL_SUMMARY,
            description = CitySpec.GET_ALL_DESCRIPTION,
            requestBody = @RequestBody(
                    description = "Request body for filtering, sorting, and paginating cities",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CityFindAllRequest.class),
                            examples = @ExampleObject(
                                    name = CitySpec.GET_ALL_EXAMPLE_NAME,
                                    value = CitySpec.GET_ALL_EXAMPLE_VALUE, 
                                    description = CitySpec.GET_ALL_DESCRIPTION
                            )
                    )
            )
    )
    public Mono<ServerResponse> getAllCity(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");

        return request.bodyToMono(CityFindAllRequest.class)
                .flatMap(r -> getAllCityUseCase.query(r, token)
                        .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                        .flatMap(wrapper -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(wrapper)
                        )
                );
    }





}
