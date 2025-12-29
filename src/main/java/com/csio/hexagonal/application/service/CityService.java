package com.csio.hexagonal.application.service;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.out.CityOutPort;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.policy.city.CityPolicy;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.Executor;

@Service
public class CityService implements CommandUseCase<CreateCityCommand, CityResponse> {

    private final CityOutPort cityOutPort;
    private final CityPolicy cityPolicy;
    private final Executor cpuExecutor;
    private final Executor virtualExecutor;

    public CityService(CityOutPort cityOutPort, CityPolicy cityPolicy, Executor cpuExecutor, Executor virtualExecutor) {
        this.cityOutPort = cityOutPort;
        this.cityPolicy = cityPolicy;
        this.cpuExecutor = cpuExecutor;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<CityResponse> create(CreateCityCommand command, String token) {

        City city = new City(
                CityId.newId(),
                command.name(),
                new State(command.state())
        );

        return Mono.fromCallable(() -> cityOutPort.findAll())
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(existing -> Mono.fromRunnable(() -> cityPolicy.ensureUnique(city, existing))
                        .subscribeOn(Schedulers.fromExecutor(cpuExecutor))
                        .then(Mono.fromCallable(() -> cityOutPort.save(city, token))
                                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))))
                .map(savedCity -> new CityResponse(
                        savedCity.getId().value().toString(),
                        savedCity.isActive(),
                        savedCity.getName(),
                        savedCity.getState().value()
                ));
    }
}
