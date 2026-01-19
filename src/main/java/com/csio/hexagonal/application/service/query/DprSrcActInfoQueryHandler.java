package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.OracleProcedurePort;
import com.csio.hexagonal.infrastructure.rest.response.corpib.DprSrcActInfoResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@Service
public class DprSrcActInfoQueryHandler
        implements QueryUseCase<DprSrcActInfoQuery, DprSrcActInfoResponse> {

    private final OracleProcedurePort procedurePort;
    private final Executor virtualExecutor;

    public DprSrcActInfoQueryHandler(
            OracleProcedurePort procedurePort,
            Executor virtualExecutor
    ) {
        this.procedurePort = procedurePort;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<DprSrcActInfoResponse> query(DprSrcActInfoQuery query, String token) {
        return Mono.fromCallable(() -> procedurePort.dprSrcActInfo(query, token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor));
    }
}
