
package com.csio.hexagonal.infrastructure.rest.in.adaptor;

import com.csio.hexagonal.application.port.in.CreateCityUseCase;
import com.csio.hexagonal.infrastructure.rest.mapper.CityRestMapper;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CreateCityUseCase useCase;
    private final CityRestMapper mapper;

    public CityController(CreateCityUseCase useCase, CityRestMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @PostMapping
    public void create(@RequestBody CreateCityRequest request) {
        useCase.create(mapper.toCommand(request));
    }
}
