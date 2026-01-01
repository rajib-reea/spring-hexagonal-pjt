package com.csio.hexagonal.application.service;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityPersistencePort;
import com.csio.hexagonal.application.query.GetCityQuery;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.infrastructure.rest.handler.CityHandler;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@Service
public class GetCityUseCase implements QueryUseCase<GetCityQuery, CityResponse> {
    private static final Logger log = LoggerFactory.getLogger(GetCityUseCase.class);

    private final CityPersistencePort cityPersistencePort;
    private final Executor virtualExecutor;

    public GetCityUseCase(
            CityPersistencePort cityPersistencePort,
            Executor virtualExecutor
    ) {
        this.cityPersistencePort = cityPersistencePort;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<CityResponse> query(GetCityQuery query, String token) {

        // Convert UUID from query to CityId value object
        CityId cityId = new CityId(query.uid());
        log.info("Received CityId  for cityId={}", cityId);
        return Mono.fromCallable(() -> cityPersistencePort.findByUid(cityId.value(), token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(Mono::justOrEmpty) // unwrap Optional<City>
                .map(city -> new CityResponse(
                        city.getId().value().toString(),
                        city.isActive(),
                        city.getName(),
                        city.getState().value()
                ));
    }
}
