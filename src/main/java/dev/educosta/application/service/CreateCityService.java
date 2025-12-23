
package dev.educosta.application.service;

import dev.educosta.application.port.in.CreateCityUseCase;
import dev.educosta.application.port.out.CityRepository;
import dev.educosta.application.usecase.CreateCityCommand;
import dev.educosta.domain.model.City;
import dev.educosta.domain.service.CityUniquenessChecker;
import dev.educosta.domain.vo.CityId;
import dev.educosta.domain.vo.State;

public class CreateCityService implements CreateCityUseCase {

    private final CityRepository repository;
    private final CityUniquenessChecker checker;

    public CreateCityService(CityRepository repository, CityUniquenessChecker checker) {
        this.repository = repository;
        this.checker = checker;
    }

    @Override
    public void create(CreateCityCommand command) {
        City city = new City(
                CityId.newId(),
                command.name(),
                new State(command.state())
        );
        checker.ensureUnique(city, repository.findAll());
        repository.save(city);
    }
}
