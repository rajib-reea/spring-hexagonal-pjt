//package com.csio.hexagonal.application.service.query;
//
//import com.csio.hexagonal.application.port.in.QueryUseCase;
//import com.csio.hexagonal.application.port.out.CityServiceContract;
//import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
//import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.List;
//import java.util.concurrent.Executor;
//
//@Service
//public class GetAllCityQueryHandler implements QueryUseCase<CityFindAllRequest, List<CityResponse>> {
//
//    private static final Logger log = LoggerFactory.getLogger(GetAllCityQueryHandler.class);
//
//    private final CityServiceContract cityServiceContract;
//    private final Executor virtualExecutor;
//
//    public GetAllCityQueryHandler(CityServiceContract cityServiceContract, Executor virtualExecutor) {
//        this.cityServiceContract = cityServiceContract;
//        this.virtualExecutor = virtualExecutor;
//    }
//
//    @Override
//    public Mono<List<CityResponse>> query(CityFindAllRequest request, String token) {
//        log.info("Fetching cities with filterGroups={}, page={}, size={}, sort={}",
//                request.filterGroups(), request.page(), request.size(), request.sort());
//
//        return Mono.fromCallable(() ->
//                        cityServiceContract.findAllWithFilters(request, token) // <-- service handles filterGroups
//                )
//                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
//                .map(list -> list.stream()
//                        .map(city -> new CityResponse(
//                                city.getId().value().toString(),
//                                city.isActive(),
//                                city.getName(),
//                                city.getState().value()
//                        ))
//                        .toList()
//                );
//    }
//}
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

    @Override
    public Mono<List<CityResponse>> query(CityFindAllRequest request, String token) {

        boolean hasFilters =
                request.filterGroups() != null && !request.filterGroups().isEmpty();

        log.info(
                "Fetching cities | hasFilters={} | page={} | size={} | sort={}",
                hasFilters,
                request.page(),
                request.size(),
                request.sort()
        );

        return Mono.fromCallable(() -> {
                    // ✅ Decision point: filters vs pagination
                    if (hasFilters) {
                        return cityServiceContract.findAllWithFilters(request, token);
                    }

                    // ✅ Ignore top-level operator when no filters exist
                    return cityServiceContract.findAllWithPagination(
                            request.page() - 1,          // 1-based → 0-based
                            request.size(),
                            null,                         // search handled separately
                            buildSortString(request),
                            token
                    );
                })
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .map(cities ->
                        cities.stream()
                                .map(city -> new CityResponse(
                                        city.getId().value().toString(),
                                        city.isActive(),
                                        city.getName(),
                                        city.getState().value()
                                ))
                                .toList()
                );
    }

    /**
     * Converts SortOrder list into "field,direction" format
     * required by existing pagination API.
     */
    private String buildSortString(CityFindAllRequest request) {
        if (request.sort() == null || request.sort().isEmpty()) {
            return "name,asc"; // default
        }

        var first = request.sort().getFirst();
        return first.field() + "," + first.direction().name().toLowerCase();
    }
}
