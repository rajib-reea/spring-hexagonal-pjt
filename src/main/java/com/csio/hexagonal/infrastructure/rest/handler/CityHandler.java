// package com.csio.hexagonal.infrastructure.rest.handler;

// import com.csio.hexagonal.application.port.in.CommandUseCase;
// import com.csio.hexagonal.domain.model.City;
// import com.csio.hexagonal.infrastructure.rest.mapper.CityMapper;
// import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
// import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
// import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
// import io.swagger.v3.oas.annotations.Operation;
// import org.springframework.stereotype.Component;
// import org.springframework.web.reactive.function.server.ServerRequest;
// import org.springframework.web.reactive.function.server.ServerResponse;
// import reactor.core.publisher.Mono;

// @Component
// public class CityHandler {

//     private final CommandUseCase<City, CityResponse> commandUseCase;
//     private final CityMapper mapper;
//      private final String entityName = "City";


//     public CityHandler(
//             CommandUseCase<City, CityResponse> commandUseCase,
//             CityMapper mapper
//     ) {
//         this.commandUseCase = commandUseCase;
//         this.mapper = mapper;
//     }

//     @Operation(
//         summary = CitySpec.CREATE_SUMMARY,
//         description = CitySpec.CREATE_DESCRIPTION
//     )
//     public Mono<ServerResponse> createCity(ServerRequest request) {

//         String token = request.headers()
//                               .firstHeader("Authorization");

//         return request.bodyToMono(CreateCityRequest.class)
//                 .map(mapper::toModel)
//                 .map(model -> commandUseCase.create(model, token))
//                 .flatMap(result -> ServerResponse.ok().bodyValue(result));
//     }
// }
package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.concurrent.Executor;
import reactor.core.scheduler.Schedulers;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CityHandler {

    private static final Logger log = LoggerFactory.getLogger(CityHandler.class);

    private final CommandUseCase<CreateCityCommand, CityResponse> commandUseCase;
    private final Executor virtualExecutor;

    public CityHandler(
            CommandUseCase<CreateCityCommand, CityResponse> commandUseCase,
            @Qualifier("virtualExecutor") Executor virtualExecutor
    ) {
        this.commandUseCase = commandUseCase;
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
                // Use reactive create (already returns Mono) and ensure it's subscribed on virtualExecutor
                .flatMap(cmd -> commandUseCase.create(cmd, token)
                    .subscribeOn(Schedulers.fromExecutor(virtualExecutor)))
            .doOnNext(res -> log.info("Service returned response: {}", res))
            .flatMap(res -> ServerResponse.ok().bodyValue(res));
    }
}
