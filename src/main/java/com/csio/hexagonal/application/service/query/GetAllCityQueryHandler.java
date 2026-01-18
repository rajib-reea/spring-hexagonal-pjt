package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.infrastructure.rest.request.CityFindAllRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.concurrent.Executor;

@Service
public class GetAllCityQueryHandler
        implements  QueryUseCase<CityFindAllRequest, PageResult<City>> {

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
    public Mono<PageResult<City>> query(CityFindAllRequest request, String token) {

        boolean hasFilters = request.filter() != null
                && request.filter().filterGroups() != null
                && !request.filter().filterGroups().isEmpty();

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
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor));
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