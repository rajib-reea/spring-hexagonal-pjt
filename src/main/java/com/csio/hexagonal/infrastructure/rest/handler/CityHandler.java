package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.service.command.CreateCityCommand;
import com.csio.hexagonal.application.service.query.GetAllCityQuery;
import com.csio.hexagonal.application.service.query.GetCityQuery;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.mapper.CityDtoMapper;
import com.csio.hexagonal.infrastructure.rest.response.helper.ResponseHelper;
import com.csio.hexagonal.infrastructure.rest.request.CityCreateRequest;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
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

    private final CommandUseCase<CreateCityCommand, City> commandUseCase;
    private final QueryUseCase<GetCityQuery, City> getCityUseCase;
    private final QueryUseCase<CityFilterQuery, PageResult<City>> getAllCityUseCase;
    private final Executor virtualExecutor;

    public CityHandler(
            CommandUseCase<CreateCityCommand, City> commandUseCase,
            QueryUseCase<GetCityQuery, City> getCityUseCase,
            QueryUseCase<CityFilterQuery, PageResult<City>> getAllCityUseCase,
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
                .doOnNext(city -> log.info("Service returned City: {}", city))
                .map(CityDtoMapper::toResponse)  // Map domain model to DTO at infrastructure boundary
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
                .map(CityDtoMapper::toResponse)  // Map domain model to DTO at infrastructure boundary
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
                .map(this::toCityFilterQuery) // Map infrastructure DTO to application query
                .flatMap(cityRequest -> getAllCityUseCase.query(cityRequest, token)
                        .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                        .map(pageResult -> {
                            // Map domain models to response DTOs
                            List<CityResponse> responseDtos = pageResult.content().stream()
                                    .map(CityDtoMapper::toResponse)
                                    .toList();
                            
                            // Create PageResult with response DTOs
                            PageResult<CityResponse> responsePage = PageResult.of(
                                    responseDtos,
                                    pageResult.page(),
                                    pageResult.size(),
                                    pageResult.totalElements(),
                                    pageResult.totalPages()
                            );
                            
                            // Convert to infrastructure wrapper
                            return CityDtoMapper.toPageResponseWrapper(responsePage);
                        })
                        .flatMap(wrapper -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(wrapper)
                        )
                );
    }

    /**
     * Maps infrastructure DTO (CityFindAllRequest) to application query object (CityFilterQuery).
     * This mapping happens at the infrastructure boundary to maintain proper dependency direction.
     */
    private CityFilterQuery toCityFilterQuery(CityFindAllRequest request) {
        return new CityFilterQuery(
                mapFilter(request.filter()),
                request.page(),
                request.size(),
                request.search(),
                mapSortOrders(request.sort())
        );
    }

    private CityFilterQuery.Filter mapFilter(CityFindAllRequest.Filter filter) {
        if (filter == null) {
            return null;
        }
        return new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.valueOf(filter.operator().name()),
                filter.filterGroups() == null ? null :
                        filter.filterGroups().stream()
                                .map(this::mapFilterGroup)
                                .toList()
        );
    }

    private CityFilterQuery.FilterGroup mapFilterGroup(CityFindAllRequest.FilterGroup group) {
        return new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.valueOf(group.operator().name()),
                group.conditions().stream()
                        .map(this::mapFilterCondition)
                        .toList()
        );
    }

    private CityFilterQuery.FilterCondition mapFilterCondition(CityFindAllRequest.FilterCondition condition) {
        return new CityFilterQuery.FilterCondition(
                condition.field(),
                CityFilterQuery.Operator.valueOf(condition.operator().name()),
                condition.value()
        );
    }

    private List<CityFilterQuery.SortOrder> mapSortOrders(List<CityFindAllRequest.SortOrder> sortOrders) {
        if (sortOrders == null) {
            return null;
        }
        return sortOrders.stream()
                .map(so -> new CityFilterQuery.SortOrder(
                        so.field(),
                        CityFilterQuery.Direction.valueOf(so.direction().name())
                ))
                .toList();
    }
}