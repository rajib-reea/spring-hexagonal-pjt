package com.csio.hexagonal.application.service;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityPersistencePort;
import com.csio.hexagonal.application.query.GetCityQuery;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.Executor;

@Service
public class GetCityUseCase implements QueryUseCase<GetCityQuery, CityResponse> {

    private final CityPersistencePort cityPersistencePort;
    private final Executor cpuExecutor;
    private final Executor virtualExecutor;

    public GetCityUseCase(
            CityPersistencePort cityPersistencePort,
            Executor cpuExecutor,
            Executor virtualExecutor
    ) {
        this.cityPersistencePort = cityPersistencePort;
        this.cpuExecutor = cpuExecutor;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<CityResponse> query(GetCityQuery query, String token) {
        // Convert string UID from HTTP path to UUID
        UUID uid = UUID.fromString(String.valueOf(query.uid()));

        return Mono.fromCallable(() -> cityPersistencePort.findByUid(uid, token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(optionalCity ->
                        Mono.justOrEmpty(optionalCity) // unwrap Optional<City>
                                .flatMap(city ->
                                        Mono.fromCallable(() -> new CityResponse(
                                                city.getId().value().toString(),
                                                city.isActive(),
                                                city.getName(),
                                                city.getState().value()
                                        )).subscribeOn(Schedulers.fromExecutor(cpuExecutor))
                                )
                );
    }
}
