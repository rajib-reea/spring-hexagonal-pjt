package com.csio.hexagonal.application.service.command;

import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.exception.DuplicateCityException;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.policy.city.CityPolicy;
import com.csio.hexagonal.domain.vo.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCityCommandHandlerTest {

    @Mock
    private CityServiceContract cityServiceContract;

    @Mock
    private CityPolicy cityPolicy;

    private Executor cpuExecutor;
    private Executor virtualExecutor;

    private CreateCityCommandHandler handler;

    @BeforeEach
    void setUp() {
        cpuExecutor = Executors.newSingleThreadExecutor();
        virtualExecutor = Executors.newSingleThreadExecutor();
        handler = new CreateCityCommandHandler(cityServiceContract, cityPolicy, cpuExecutor, virtualExecutor);
    }

    @Test
    void shouldCreateCitySuccessfully() {
        // Arrange
        CreateCityCommand command = new CreateCityCommand("New York", "NY");
        String token = "test-token";
        List<City> existingCities = Collections.emptyList();

        when(cityServiceContract.findAll(token)).thenReturn(existingCities);
        doNothing().when(cityPolicy).ensureUnique(any(City.class), anyList());
        when(cityServiceContract.save(any(City.class), eq(token))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<City> result = handler.create(command, token);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(city ->
                        city.getName().equals("New York") &&
                                city.getState().equals(new State("NY")) &&
                                city.isActive()
                )
                .verifyComplete();

        verify(cityServiceContract).findAll(token);
        verify(cityPolicy).ensureUnique(any(City.class), eq(existingCities));
        verify(cityServiceContract).save(any(City.class), eq(token));
    }

    @Test
    void shouldThrowExceptionWhenCityAlreadyExists() {
        // Arrange
        CreateCityCommand command = new CreateCityCommand("New York", "NY");
        String token = "test-token";
        List<City> existingCities = Collections.emptyList();

        when(cityServiceContract.findAll(token)).thenReturn(existingCities);
        doThrow(new DuplicateCityException("New York"))
                .when(cityPolicy).ensureUnique(any(City.class), anyList());

        // Act
        Mono<City> result = handler.create(command, token);

        // Assert
        StepVerifier.create(result)
                .expectError(DuplicateCityException.class)
                .verify();

        verify(cityServiceContract).findAll(token);
        verify(cityPolicy).ensureUnique(any(City.class), eq(existingCities));
        verify(cityServiceContract, never()).save(any(City.class), anyString());
    }

    @Test
    void shouldPassTokenToPersistencePort() {
        // Arrange
        CreateCityCommand command = new CreateCityCommand("Los Angeles", "CA");
        String token = "custom-token";
        List<City> existingCities = Collections.emptyList();

        when(cityServiceContract.findAll(token)).thenReturn(existingCities);
        doNothing().when(cityPolicy).ensureUnique(any(City.class), anyList());
        when(cityServiceContract.save(any(City.class), eq(token))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<City> result = handler.create(command, token);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();

        verify(cityServiceContract).findAll(token);
        verify(cityServiceContract).save(any(City.class), eq(token));
    }
}
