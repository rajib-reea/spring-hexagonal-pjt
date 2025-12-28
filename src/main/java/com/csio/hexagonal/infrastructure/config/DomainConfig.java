
package com.csio.hexagonal.infrastructure.config;

import com.csio.hexagonal.application.port.in.CreateCityUseCase;
import com.csio.hexagonal.application.port.out.CityRepository;
import com.csio.hexagonal.application.service.CreateCityService;
import com.csio.hexagonal.domain.service.CityUniquenessChecker;
import com.csio.hexagonal.domain.service.impl.CityUniquenessCheckerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    CityUniquenessChecker cityUniquenessChecker() {
        return new CityUniquenessCheckerImpl();
    }

    @Bean
    CreateCityUseCase createCityUseCase(
            CityRepository repository,
            CityUniquenessChecker checker
    ) {
        return new CreateCityService(repository, checker);
    }
}
