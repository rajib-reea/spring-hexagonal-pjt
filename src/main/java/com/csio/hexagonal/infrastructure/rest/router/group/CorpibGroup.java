package com.csio.hexagonal.infrastructure.rest.router.group;

import com.csio.hexagonal.infrastructure.rest.router.group.contract.GroupedOpenApiProvider;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorpibGroup implements GroupedOpenApiProvider {

    @Bean
    public GroupedOpenApi corpibOpenApi(@Value("${springdoc.version}") String appVersion) {
        return createGroupedOpenApi("corpib", "CORPIB Procedures", "/api/v1/corpib/**", appVersion);
    }
}
