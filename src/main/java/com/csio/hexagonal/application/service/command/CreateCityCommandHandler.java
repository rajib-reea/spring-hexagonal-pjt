package com.csio.hexagonal.application.service.command;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.out.CityServiceContract;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.policy.city.CityPolicy;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.concurrent.Executor;

@Service
public class CreateCityCommandHandler implements CommandUseCase<CreateCityCommand, City> {

    private final CityServiceContract cityPersistencePort;
    private final CityPolicy cityPolicy;
    private final Executor cpuExecutor;
    private final Executor virtualExecutor;

    public CreateCityCommandHandler(CityServiceContract cityPersistencePort, CityPolicy cityPolicy, Executor cpuExecutor, Executor virtualExecutor) {
        this.cityPersistencePort = cityPersistencePort;
        this.cityPolicy = cityPolicy;
        this.cpuExecutor = cpuExecutor;
        this.virtualExecutor = virtualExecutor;
    }

    @Override
    public Mono<City> create(CreateCityCommand command, String token) {

        City city = new City(
                CityId.newId(),
                command.name(),
                new State(command.state())
        );

        return Mono.fromCallable(() -> cityPersistencePort.findAll(token))  // <-- pass token
                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))
                .flatMap(existing -> Mono.fromRunnable(() -> cityPolicy.ensureUnique(city, existing))
                        .subscribeOn(Schedulers.fromExecutor(cpuExecutor))
                        .then(Mono.fromCallable(() -> cityPersistencePort.save(city, token))
                                .subscribeOn(Schedulers.fromExecutor(virtualExecutor))));
    }
}
