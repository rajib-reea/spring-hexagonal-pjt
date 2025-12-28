
package com.csio.hexagonal.application.service;

import com.csio.hexagonal.application.port.in.CreateCityUseCase;
import com.csio.hexagonal.application.port.out.CityRepository;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.service.CityUniquenessChecker;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;

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
