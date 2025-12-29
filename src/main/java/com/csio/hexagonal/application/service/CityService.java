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

@Service
public class CityService implements CommandUseCase<CreateCityCommand, CityResponse> {

    private final CityOutPort cityOutPort;
    private final CityPolicy cityPolicy;

    public CityService(CityOutPort cityOutPort, CityPolicy cityPolicy) {
        this.cityOutPort = cityOutPort;
        this.cityPolicy = cityPolicy;
    }

    @Override
    public CityResponse create(CreateCityCommand command, String token) {

        City city = new City(
                CityId.newId(),
                command.name(),
                new State(command.state())
        );

        cityPolicy.ensureUnique(city, cityOutPort.findAll());

        City savedCity = cityOutPort.save(city);

        return new CityResponse(
                savedCity.getId().value().toString(), // UUID → String
                savedCity.isActive(),
                savedCity.getName(),
                savedCity.getState().value()
        );
    }
}
