package com.csio.hexagonal.infrastructure.config.router.group;

import com.csio.hexagonal.infrastructure.config.router.group.contract.GroupedOpenApiProvider;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CityGroup implements GroupedOpenApiProvider {

    @Bean
    public GroupedOpenApi cityOpenApi(@Value("${springdoc.version}") String appVersion) {
        return createGroupedOpenApi("city", "City API", "/api/v1/city/**", appVersion);
    }
}