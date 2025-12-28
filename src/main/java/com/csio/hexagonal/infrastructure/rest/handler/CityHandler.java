package com.csio.hexagonal.infrastructure.rest.in.adaptor;

import com.csio.hexagonal.application.port.in.CreateCityUseCase;
import com.csio.hexagonal.infrastructure.rest.spec.CitySpec;
import com.csio.hexagonal.infrastructure.rest.mapper.CityRestMapper;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
// import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
public class CityHandler {

    private final CreateCityUseCase useCase;
    private final CityRestMapper mapper;

    public CityHandler(CreateCityUseCase useCase, CityRestMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @Operation(
        summary = CitySpec.CREATE_SUMMARY,
        description = CitySpec.CREATE_DESCRIPTION
    )
    @PostMapping
    public void create(
        @RequestBody(
            description = CitySpec.CREATE_DESCRIPTION,
            required = true,
            content = @Content(
                schema = @Schema(implementation = CreateCityRequest.class),
                examples = @ExampleObject(
                    name = CitySpec.CREATE_EXAMPLE_NAME,
                    description = CitySpec.CREATE_EXAMPLE_DESCRIPTION,
                    value = CitySpec.CREATE_EXAMPLE_VALUE
                )
            )
        )
       @org.springframework.web.bind.annotation.RequestBody CreateCityRequest request
    ) {
        useCase.create(mapper.toCommand(request));
    }
}
