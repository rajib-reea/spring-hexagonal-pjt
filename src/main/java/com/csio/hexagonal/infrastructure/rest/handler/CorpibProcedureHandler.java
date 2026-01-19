package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.service.query.DprSrcActInfoQuery;
import com.csio.hexagonal.infrastructure.rest.request.DprSrcActInfoRequest;
import com.csio.hexagonal.infrastructure.rest.response.corpib.DprSrcActInfoResponse;
import com.csio.hexagonal.infrastructure.rest.response.helper.ResponseHelper;
import com.csio.hexagonal.infrastructure.rest.spec.CorpibSpec;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@Component
public class CorpibProcedureHandler {

    private final QueryUseCase<DprSrcActInfoQuery, DprSrcActInfoResponse> dprSrcActInfoUseCase;
    private final Executor virtualExecutor;

    public CorpibProcedureHandler(
            QueryUseCase<DprSrcActInfoQuery, DprSrcActInfoResponse> dprSrcActInfoUseCase,
            @Qualifier("virtualExecutor") Executor virtualExecutor
    ) {
        this.dprSrcActInfoUseCase = dprSrcActInfoUseCase;
        this.virtualExecutor = virtualExecutor;
    }

    @Operation(
            summary = CorpibSpec.DPR_SRC_ACT_INFO_SUMMARY,
            description = CorpibSpec.DPR_SRC_ACT_INFO_DESCRIPTION
    )
    @RequestBody(
            description = CorpibSpec.DPR_SRC_ACT_INFO_DESCRIPTION,
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = DprSrcActInfoRequest.class),
                    examples = @ExampleObject(
                            name = CorpibSpec.DPR_SRC_ACT_INFO_EXAMPLE_NAME,
                            value = CorpibSpec.DPR_SRC_ACT_INFO_EXAMPLE_VALUE
                    )
            )
    )
    public Mono<ServerResponse> dprSrcActInfo(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");

        return request.bodyToMono(DprSrcActInfoRequest.class)
                .map(req -> new DprSrcActInfoQuery(req.userCode(), req.orgCode(), req.actNum()))
                .flatMap(query -> dprSrcActInfoUseCase.query(query, token)
                        .subscribeOn(Schedulers.fromExecutor(virtualExecutor)))
                .map(ResponseHelper::success)
                .flatMap(wrapper -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(wrapper));
    }
}
