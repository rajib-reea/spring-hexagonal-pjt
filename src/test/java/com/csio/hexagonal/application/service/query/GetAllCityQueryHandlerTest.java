package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.out.CityContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllCityQueryHandlerTest {

    @Mock
    private CityContract cityServiceContract;

    private Executor virtualExecutor;

    private GetAllCityQueryHandler handler;

    @BeforeEach
    void setUp() {
        virtualExecutor = Executors.newSingleThreadExecutor();
        handler = new GetAllCityQueryHandler(cityServiceContract, virtualExecutor);
    }

    @Test
    void shouldReturnPageResultWithPagination() {
        // Arrange
        City city1 = new City(CityId.newId(), "New York", new State("NY"));
        City city2 = new City(CityId.newId(), "Los Angeles", new State("CA"));
        List<City> cities = Arrays.asList(city1, city2);
        
        PageResult<City> expectedResult = PageResult.of(cities, 1, 10, 2, 1);
        
        CityFilterQuery query = new CityFilterQuery(null, 1, 10, null, null);
        String token = "test-token";

        when(cityServiceContract.findAllWithPagination(eq(1), eq(10), isNull(), anyString(), eq(token)))
                .thenReturn(expectedResult);

        // Act
        Mono<PageResult<City>> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(pageResult ->
                        pageResult.content().size() == 2 &&
                                pageResult.page() == 1 &&
                                pageResult.size() == 10 &&
                                pageResult.totalElements() == 2
                )
                .verifyComplete();

        verify(cityServiceContract).findAllWithPagination(eq(1), eq(10), isNull(), anyString(), eq(token));
    }

    @Test
    void shouldReturnPageResultWithFilters() {
        // Arrange
        City city1 = new City(CityId.newId(), "New York", new State("NY"));
        List<City> cities = Arrays.asList(city1);
        
        PageResult<City> expectedResult = PageResult.of(cities, 1, 10, 1, 1);
        
        CityFilterQuery.FilterCondition condition = new CityFilterQuery.FilterCondition(
                "name", CityFilterQuery.Operator.EQUALS, "New York");
        CityFilterQuery.FilterGroup group = new CityFilterQuery.FilterGroup(
                CityFilterQuery.LogicalOperator.AND, List.of(condition));
        CityFilterQuery.Filter filter = new CityFilterQuery.Filter(
                CityFilterQuery.LogicalOperator.AND, List.of(group));
        
        CityFilterQuery query = new CityFilterQuery(filter, 1, 10, null, null);
        String token = "test-token";

        when(cityServiceContract.findAllWithFilters(eq(query), eq(token)))
                .thenReturn(expectedResult);

        // Act
        Mono<PageResult<City>> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(pageResult ->
                        pageResult.content().size() == 1 &&
                                pageResult.totalElements() == 1
                )
                .verifyComplete();

        verify(cityServiceContract).findAllWithFilters(eq(query), eq(token));
    }

    @Test
    void shouldReturnPageResultWithSearch() {
        // Arrange
        City city1 = new City(CityId.newId(), "New York", new State("NY"));
        List<City> cities = Arrays.asList(city1);
        
        PageResult<City> expectedResult = PageResult.of(cities, 1, 10, 1, 1);
        
        CityFilterQuery query = new CityFilterQuery(null, 1, 10, "New", null);
        String token = "test-token";

        when(cityServiceContract.findAllWithPagination(eq(1), eq(10), eq("New"), anyString(), eq(token)))
                .thenReturn(expectedResult);

        // Act
        Mono<PageResult<City>> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(pageResult ->
                        pageResult.content().size() == 1
                )
                .verifyComplete();

        verify(cityServiceContract).findAllWithPagination(eq(1), eq(10), eq("New"), anyString(), eq(token));
    }

    @Test
    void shouldPassTokenToPersistencePort() {
        // Arrange
        PageResult<City> expectedResult = PageResult.of(List.of(), 1, 10, 0, 0);
        
        CityFilterQuery query = new CityFilterQuery(null, 1, 10, null, null);
        String token = "custom-token";

        when(cityServiceContract.findAllWithPagination(anyInt(), anyInt(), isNull(), anyString(), eq(token)))
                .thenReturn(expectedResult);

        // Act
        Mono<PageResult<City>> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(cityServiceContract).findAllWithPagination(anyInt(), anyInt(), isNull(), anyString(), eq(token));
    }
}
