package com.csio.hexagonal.infrastructure.config;

import com.csio.hexagonal.domain.policy.city.CityPolicy;
import com.csio.hexagonal.domain.policy.city.CityPolicyEnforcer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyConfig {

    @Bean
    public CityPolicy cityPolicy() {
        return new CityPolicyEnforcer();
    }
}
