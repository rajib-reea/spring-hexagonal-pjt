package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Executor;

@Service
public class GetAllCityQueryHandler
        implements QueryUseCase<CityFindAllRequest, List<CityResponse>> {

    private static final Logger log =
            LoggerFactory.getLogger(GetAllCityQueryHandler.class);

    private final CityServiceContract cityServiceContract;
    private final Executor virtualExecutor;

    public GetAllCityQueryHandler(
            CityServiceContract cityServiceContract,
            Executor virtualExecutor
    ) {
        this.cityServiceContract = cityServiceContract;
        this.virtualExecutor = virtualExecutor;
    }

    //@Override
//    public Mono<List<CityResponse>> query(CityFindAllRequest request, String token) {
//
//        boolean hasFilters = request.filter() != null
//                && request.filter().filterGroups() != null
//                && !request.filter().filterGroups().isEmpty();
//
//        log.info(
//                "Fetching cities | hasFilters={} | search={} | page={} | size={} | sort={}",
//                hasFilters,
//                request.search(),
//                request.page(),
//                request.size(),
//                request.sort()
//        );
//
//        return Mono.fromCallable(() -> {
//                    if (hasFilters) {
//                        return cityServiceContract.findAllWithFilters(request, token);
//                    }
//
//                    // No filters: delegate to pagination, support multiple sort orders
//                    return cityServiceContract.findAllWithPagination(
//                            request.page(),           // keep 1-based pagination
//                            request.size(),
//                            request.search(),
//                            buildSortString(request),
//                            token
//                    );
//                })
//                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
//                .map(cities ->
//                        cities.stream()
//                                .map(city -> new CityResponse(
//                                        city.getId().value().toString(),
//                                        city.isActive(),
//                                        city.getName(),
//                                        city.getState().value()
//                                ))
//                                .toList()
//                );
//    }
    @Override
    public Mono<List<CityResponse>> query(CityFindAllRequest request, String token) {

        boolean hasFilters = request.filter() != null
                && request.filter().filterGroups() != null
                && !request.filter().filterGroups().isEmpty();

        log.info(
                "Fetching cities | hasFilters={} | search={} | page={} | size={} | sort={}",
                hasFilters,
                request.search(),
                request.page(),
                request.size(),
                request.sort()
        );

        return Mono.fromCallable(() -> {
                    if (hasFilters) {
                        return cityServiceContract.findAllWithFilters(request, token);
                    } else {
                        return cityServiceContract.findAllWithPagination(
                                request.page(),
                                request.size(),
                                request.search(),
                                buildSortString(request),
                                token
                        );
                    }
                })
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .map(wrapper -> wrapper.content()); // extract list of CityResponse
    }


    /**
     * Converts SortOrder list into a comma-separated string
     * "field,direction;field,direction" format for pagination API.
     * Supports multiple sort orders.
     */
    private String buildSortString(CityFindAllRequest request) {
        if (request.sort() == null || request.sort().isEmpty()) {
            return "name,asc"; // default
        }

        return request.sort().stream()
                .map(order -> order.field() + "," + order.direction().name().toLowerCase())
                .reduce((s1, s2) -> s1 + ";" + s2) // combine multiple sort orders
                .orElse("name,asc");
    }
}