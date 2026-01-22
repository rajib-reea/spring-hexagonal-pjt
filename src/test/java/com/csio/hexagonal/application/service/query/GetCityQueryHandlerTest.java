package com.csio.hexagonal.application.service.query;

import com.csio.hexagonal.application.port.out.CityContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCityQueryHandlerTest {

    @Mock
    private CityContract cityServiceContract;

    private Executor virtualExecutor;

    private GetCityQueryHandler handler;

    @BeforeEach
    void setUp() {
        virtualExecutor = Executors.newSingleThreadExecutor();
        handler = new GetCityQueryHandler(cityServiceContract, virtualExecutor);
    }

    @Test
    void shouldReturnCityWhenFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        GetCityQuery query = new GetCityQuery(uuid);
        String token = "test-token";
        
        City expectedCity = new City(new CityId(uuid), "New York", new State("NY"));
        when(cityServiceContract.findByUid(eq(uuid), eq(token))).thenReturn(Optional.of(expectedCity));

        // Act
        Mono<City> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(city ->
                        city.getName().equals("New York") &&
                                city.getState().equals(new State("NY"))
                )
                .verifyComplete();

        verify(cityServiceContract).findByUid(eq(uuid), eq(token));
    }

    @Test
    void shouldReturnEmptyWhenCityNotFound() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        GetCityQuery query = new GetCityQuery(uuid);
        String token = "test-token";

        when(cityServiceContract.findByUid(eq(uuid), eq(token))).thenReturn(Optional.empty());

        // Act
        Mono<City> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(cityServiceContract).findByUid(eq(uuid), eq(token));
    }

    @Test
    void shouldPassTokenToPersistencePort() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        GetCityQuery query = new GetCityQuery(uuid);
        String token = "custom-token";
        
        City expectedCity = new City(new CityId(uuid), "Los Angeles", new State("CA"));
        when(cityServiceContract.findByUid(eq(uuid), eq(token))).thenReturn(Optional.of(expectedCity));

        // Act
        Mono<City> result = handler.query(query, token);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(cityServiceContract).findByUid(eq(uuid), eq(token));
    }
}
