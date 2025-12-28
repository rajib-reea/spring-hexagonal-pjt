package com.csio.hexagonal.application.service;

import com.csio.hexagonal.application.port.in.CommandUseCase;
import com.csio.hexagonal.application.port.out.CityOutPort;
import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.service.CityUniquenessChecker;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import org.springframework.stereotype.Service;


@Service
public class CityService implements CommandUseCase<CreateCityCommand, CityResponse>  {

    private final CityOutPort cityOutPort;
    private final CityUniquenessChecker checker;

    public CityService(CityOutPort cityOutPort, CityUniquenessChecker checker) {
        this.cityOutPort = cityOutPort;
        this.checker = checker;
    }

    @Override
    public CityResponse create(CreateCityCommand command, String token) {
        City city = new City(
                CityId.newId(),
                command.name(),
                new State(command.state())
        );
        checker.ensureUnique(city, cityOutPort.findAll());
        City savedCity = cityOutPort.save(city);
        //return new CityResponse(city.name(), city.state());
        return new CityResponse(
                savedCity.id().value(),     // uid
                savedCity.isActive(),       // isActive
                savedCity.name(),           // name
                savedCity.state().value()   // state
        );
    }
}
