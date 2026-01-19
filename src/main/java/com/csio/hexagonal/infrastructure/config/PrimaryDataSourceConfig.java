package com.csio.hexagonal.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Primary datasource configuration to keep the existing JPA setup intact
 * when adding additional datasources.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.csio.hexagonal.infrastructure.store.persistence.adapter",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "primaryDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource primaryDataSource(
            @Qualifier("primaryDataSourceProperties") DataSourceProperties dsp
    ) {
        return dsp.initializeDataSourceBuilder().build();
    }

    @Bean(name = "primaryEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource primaryDataSource,
            JpaProperties jpaProperties
    ) {
        return builder
                .dataSource(primaryDataSource)
                .packages("com.csio.hexagonal.infrastructure.store.persistence.entity")
                .persistenceUnit("primaryPU")
                .properties(jpaProperties.getProperties())
                .build();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
