package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Executor;

@Service
public class GetAllCityQueryHandler implements QueryUseCase<CityFindAllRequest, List<CityResponse>> {

    private static final Logger log = LoggerFactory.getLogger(GetAllCityQueryHandler.class);

    private final CityServiceContract cityServiceContract;
    private final Executor virtualExecutor;

    public GetAllCityQueryHandler(CityServiceContract cityServiceContract, Executor virtualExecutor) {
        this.cityServiceContract = cityServiceContract;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<List<CityResponse>> query(CityFindAllRequest request, String token) {
        log.info("Fetching cities with filterGroups={}, page={}, size={}, sort={}",
                request.filterGroups(), request.page(), request.size(), request.sort());

        return Mono.fromCallable(() ->
                        cityServiceContract.findAllWithFilters(request, token) // <-- service handles filterGroups
                )
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
