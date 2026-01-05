package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.List;
import java.util.concurrent.Executor;

@Service
public class GetAllCityQueryHandler implements QueryUseCase<GetAllCityQuery, List<CityResponse>> {

    private static final Logger log = LoggerFactory.getLogger(GetAllCityQueryHandler.class);

    private final CityServiceContract cityServiceContract;
    private final Executor virtualExecutor;

    public GetAllCityQueryHandler(CityServiceContract cityServiceContract, Executor virtualExecutor) {
        this.cityServiceContract = cityServiceContract;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<List<CityResponse>> query(GetAllCityQuery query, String token) {
        log.info("Fetching all cities with page={}, size={}, search={}, sort={}",
                query.page(), query.size(), query.search(), query.sort());

        return Mono.fromCallable(() ->
                        cityServiceContract.findAllWithPagination(
                                query.page(),
                                query.size(),
                                query.search(),
                                query.sort(),
                                token))
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .map(list -> list.stream()
                        .map(city -> new CityResponse(
                                city.getId().value().toString(),
                                city.isActive(),
                                city.getName(),
                                city.getState().value()
                        ))
                        .toList()
                );
    }
}
