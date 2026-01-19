// Spring Boot provides a default transaction manager. If you have multiple databases
// (e.g., Oracle and PostgreSQL), Spring needs an explicit transaction manager per datasource.

package com.csio.hexagonal.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
public class OracleTransactionConfig {

    @Bean(name = "corpibTransactionManager")
    public PlatformTransactionManager corpibTransactionManager(
            @Qualifier("corpibEntityManagerFactory") EntityManagerFactory corpibEntityManagerFactory
    ) {
        return new JpaTransactionManager(corpibEntityManagerFactory);
    }
}
