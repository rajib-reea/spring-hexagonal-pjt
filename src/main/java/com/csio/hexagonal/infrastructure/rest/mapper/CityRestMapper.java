
// package com.csio.hexagonal.infrastructure.rest.mapper;

// import com.csio.hexagonal.application.usecase.CreateCityCommand;
// import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
// import org.springframework.stereotype.Component;

// @Component
// public class CityRestMapper {
//     public CreateCityCommand toCommand(CreateCityRequest req) {
//         return new CreateCityCommand(req.name(), req.state());
//     }
// }
package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.application.usecase.CreateCityCommand;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CityRestMapper {

    private static final Logger logger =
            LoggerFactory.getLogger(CityRestMapper.class);

    public CreateCityCommand toCommand(CreateCityRequest req) {

        logger.info(
            "Mapping CreateCityRequest to CreateCityCommand [name={}, state={}]",
            req.name(),
            req.state()
        );

        return new CreateCityCommand(req.name(), req.state());
    }
}
