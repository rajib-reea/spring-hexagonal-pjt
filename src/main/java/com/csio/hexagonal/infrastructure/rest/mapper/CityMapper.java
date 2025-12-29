
// // package com.csio.hexagonal.infrastructure.rest.mapper;

// // import com.csio.hexagonal.application.usecase.CreateCityCommand;
// // import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
// // import org.springframework.stereotype.Component;

// // @Component
// // public class CityRestMapper {
// //     public CreateCityCommand toCommand(CreateCityRequest req) {
// //         return new CreateCityCommand(req.name(), req.state());
// //     }
// // }
// package com.csio.hexagonal.infrastructure.rest.mapper;

// import com.csio.hexagonal.application.usecase.CreateCityCommand;
// import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;

// @Component
// public class CityRestMapper {

//     private static final Logger logger =
//             LoggerFactory.getLogger(CityRestMapper.class);

//     public CreateCityCommand toCommand(CreateCityRequest req) {

//         logger.info(
//             "Mapping CreateCityRequest to CreateCityCommand [name={}, state={}]",
//             req.name(),
//             req.state()
//         );

//         return new CreateCityCommand(req.name(), req.state());
//     }
// }
package com.csio.hexagonal.infrastructure.rest.mapper;

import com.csio.hexagonal.domain.model.City;
import com.csio.hexagonal.domain.vo.CityId;
import com.csio.hexagonal.domain.vo.State;
import com.csio.hexagonal.infrastructure.rest.request.CreateCityRequest;
import com.csio.hexagonal.infrastructure.rest.response.city.CityResponse;
import org.springframework.stereotype.Component;

@Component
public class CityMapper {

    /**
     * Maps REST request → Domain model
     */
    public City toModel(CreateCityRequest request) {
        return new City(
                CityId.newId(),
                request.name(),
                new State(request.state())
        );
    }

    /**
     * Maps Domain model → REST response
     */
    public CityResponse toResponse(City city) {
        return new CityResponse(
                city.id().value(),
                city.isActive(),
                city.name(),
                city.state().value()
        );
    }
}
