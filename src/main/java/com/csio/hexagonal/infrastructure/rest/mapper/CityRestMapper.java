
package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import org.springframework.stereotype.Component;

@Component
public class CityRestMapper {
    public CreateCityCommand toCommand(CreateCityRequest req) {
        return new CreateCityCommand(req.name(), req.state());
    }
}
