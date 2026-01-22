package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.Executor;

@Service
public class GetCityQueryHandler implements QueryUseCase<GetCityQuery, City> {
    private static final Logger log = LoggerFactory.getLogger(GetCityQueryHandler.class);

    private final CityContract cityServiceContract;
    private final Executor virtualExecutor;

    public GetCityQueryHandler(
            CityContract cityServiceContract,
            Executor virtualExecutor
    ) {
        this.cityServiceContract = cityServiceContract;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<City> query(GetCityQuery query, String token) {

        // Convert UUID from query to CityId value object
        CityId cityId = new CityId(query.uid());
        log.info("Received CityId  for cityId={}", cityId);
        return Mono.fromCallable(() -> cityServiceContract.findByUid(UUID.fromString(String.valueOf(cityId.value())), token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(Mono::justOrEmpty); // unwrap Optional<City>
    }
}
