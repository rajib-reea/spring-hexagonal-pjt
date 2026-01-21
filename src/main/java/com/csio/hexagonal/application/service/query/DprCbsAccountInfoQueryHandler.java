package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.OracleProcedurePort;
import com.csio.hexagonal.infrastructure.rest.response.corpib.DprCbsAccountInfoResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@Service
public class DprCbsAccountInfoQueryHandler
        implements QueryUseCase<DprCbsAccountInfoQuery, DprCbsAccountInfoResponse> {

    private final OracleProcedurePort procedurePort;
    private final Executor virtualExecutor;

    public DprCbsAccountInfoQueryHandler(
            OracleProcedurePort procedurePort,
            Executor virtualExecutor
    ) {
        this.procedurePort = procedurePort;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<DprCbsAccountInfoResponse> query(DprCbsAccountInfoQuery query, String token) {
        return Mono.fromCallable(() -> procedurePort.dprCbsAccountInfo(query, token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor));
    }
}
