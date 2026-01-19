package com.csio.hexagonal.infrastructure.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Chooses which datasource/transaction manager to use for stored procedures.
 * Use property: procedure.datasource=primary|corpib (default: primary).
 */
@Configuration
public class ProcedureDataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(ProcedureDataSourceConfig.class);

    @Bean(name = "procedureEntityManager")
    public EntityManager procedureEntityManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEmf,
            @Qualifier("corpibEntityManagerFactory") EntityManagerFactory corpibEmf,
            @Value("${procedure.datasource:primary}") String datasource
    ) {
        EntityManagerFactory emf = useCorpib(datasource) ? corpibEmf : primaryEmf;
        log.info("Procedure datasource selected: {}", useCorpib(datasource) ? "corpib" : "primary");
        return SharedEntityManagerCreator.createSharedEntityManager(emf);
    }

    @Bean(name = "procedureTransactionManager")
    public PlatformTransactionManager procedureTransactionManager(
            @Qualifier("primaryTransactionManager") PlatformTransactionManager primaryTx,
            @Qualifier("corpibTransactionManager") PlatformTransactionManager corpibTx,
            @Value("${procedure.datasource:primary}") String datasource
    ) {
        return useCorpib(datasource) ? corpibTx : primaryTx;
    }

    private boolean useCorpib(String datasource) {
        return "corpib".equalsIgnoreCase(datasource);
    }
}
