
package dev.educosta.infrastructure.config;

import dev.educosta.application.port.in.CreateCityUseCase;
import dev.educosta.application.port.out.CityRepository;
import dev.educosta.application.service.CreateCityService;
import dev.educosta.domain.service.CityUniquenessChecker;
import dev.educosta.domain.service.impl.CityUniquenessCheckerImpl;
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
