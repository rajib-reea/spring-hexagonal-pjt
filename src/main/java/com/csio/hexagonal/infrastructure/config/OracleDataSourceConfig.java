// By default, Spring Boot expects one database. It automatically configures a DataSource and
// EntityManager for you. However, if your application needs to connect to two or more databases,
// Spring needs explicit configuration for the additional data sources.

package com.csio.hexagonal.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OracleDataSourceConfig {

    /**
     * Binds properties prefixed with "spring.datasource.corpib" to DataSourceProperties.
     * Example properties keys:
     * spring.datasource.corpib.url
     * spring.datasource.corpib.username
     * spring.datasource.corpib.password
     * spring.datasource.corpib.driver-class-name
     */
    @Bean
    @ConfigurationProperties("spring.datasource.corpib")
    public DataSourceProperties corpibDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "corpibDataSource")
    @ConfigurationProperties("spring.datasource.corpib.hikari")
    public DataSource corpibDataSource(
            @Qualifier("corpibDataSourceProperties") DataSourceProperties dsp
    ) {
        // Use HikariCP as the DataSource implementation
        return dsp.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * Create an EntityManagerFactory for the CORPIB persistence unit.
     * The packagesToScan here can include shared entity classes even if the
     * CORPIB datasource is used only for procedures.
     */
    @Bean(name = "corpibEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean corpibEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("corpibDataSource") DataSource corpibDataSource
    ) {
        Map<String, Object> props = new HashMap<>();
        // Recommended: set the dialect appropriate to your Oracle version
        props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
        // Do not use hbm2ddl.auto=update/create in production; set to 'none' or remove
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.show_sql", "false");

        return builder
                .dataSource(corpibDataSource)
                .packages("com.csio.hexagonal.infrastructure.store.persistence.entity")
                .persistenceUnit("corpibPU")
                .properties(props)
                .build();
    }

    /**
     * Optional: expose an EntityManager bean bound to the corpibEntityManagerFactory.
     * This makes it straightforward to constructor-inject an EntityManager into adapters:
     *   public OracleJpaProcedureAdapter(@Qualifier("corpibEntityManager") EntityManager em) { ... }
     */
    @Bean(name = "corpibEntityManager")
    public EntityManager corpibEntityManager(
            @Qualifier("corpibEntityManagerFactory") EntityManagerFactory emf
    ) {
        return emf.createEntityManager();
    }
}
