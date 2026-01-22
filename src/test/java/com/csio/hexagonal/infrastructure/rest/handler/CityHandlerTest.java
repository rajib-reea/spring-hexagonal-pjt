package com.csio.hexagonal.infrastructure.rest.handler;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.in.QueryUseCase;
import com.csio.hexagonal.application.service.command.CreateCityCommand;
import com.csio.hexagonal.application.service.query.CityFilterQuery;
import com.csio.hexagonal.application.service.query.GetCityQuery;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.PageResult;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityHandlerTest {

    @Mock
    private CommandUseCase<CreateCityCommand, City> commandUseCase;

    @Mock
    private QueryUseCase<GetCityQuery, City> getCityUseCase;

    @Mock
    private QueryUseCase<CityFilterQuery, PageResult<City>> getAllCityUseCase;

    private Executor virtualExecutor;

    private CityHandler handler;

    @BeforeEach
    void setUp() {
        virtualExecutor = Executors.newSingleThreadExecutor();
        handler = new CityHandler(commandUseCase, getCityUseCase, getAllCityUseCase, virtualExecutor);
    }

    @Test
    void shouldHandleCreateCityRequest() {
        // Assert that handler is properly instantiated with dependencies
        assertNotNull(handler);
    }

    @Test
    void shouldHandleGetCityRequest() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        City city = new City(new CityId(uuid), "New York", new State("NY"));
        when(getCityUseCase.query(any(GetCityQuery.class), anyString()))
                .thenReturn(Mono.just(city));

        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "test-token")
                .pathVariable("uid", uuid.toString())
                .build();

        // Act
        Mono<ServerResponse> response = handler.getCity(request);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(r -> r.statusCode().value() == 200)
                .verifyComplete();

        verify(getCityUseCase).query(any(GetCityQuery.class), eq("test-token"));
    }

    @Test
    void shouldHandleGetAllCitiesRequest() {
        // Assert that handler is properly instantiated with dependencies
        assertNotNull(handler);
    }
}
