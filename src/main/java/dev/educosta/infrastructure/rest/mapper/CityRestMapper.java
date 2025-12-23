
package dev.educosta.infrastructure.rest.mapper;

import dev.educosta.application.usecase.CreateCityCommand;
import dev.educosta.infrastructure.rest.request.CreateCityRequest;
import org.springframework.stereotype.Component;

@Component
public class CityRestMapper {
    public CreateCityCommand toCommand(CreateCityRequest req) {
        return new CreateCityCommand(req.name(), req.state());
    }
}
