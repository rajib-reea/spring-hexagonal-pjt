
//By default, Spring Boot expects one database. It automatically configures a DataSource and EntityManager for you. 
//However, if your application needs to connect to two or more databases (e.g., an Oracle DB for legacy user data and a PostgreSQL DB for new transactions), Spring gets confused.

package com.csio.hexagonal.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class OracleDataSourceConfig {

    /**
     * Binds properties prefixed with "oracle.datasource" to DataSourceProperties.
     * Example properties keys:
     * oracle.datasource.url
     * oracle.datasource.username
     * oracle.datasource.password
     * oracle.datasource.driver-class-name
     */
    @Bean
    @ConfigurationProperties("oracle.datasource")
    public DataSourceProperties oracleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "oracleDataSource")
    @ConfigurationProperties("oracle.datasource.hikari")
    public DataSource oracleDataSource(@Qualifier("oracleDataSourceProperties") DataSourceProperties dsp) {
        // Use HikariCP as the DataSource implementation
        return dsp.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * Create an EntityManagerFactory for the Oracle persistence unit.
     * Adjust packagesToScan to the package(s) that contain your entity classes.
     */
    @Bean(name = "oracleEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("oracleDataSource") DataSource oracleDataSource) {

        Map<String, Object> props = new HashMap<>();
        // Recommended: set the dialect appropriate to your Oracle version
        props.put("hibernate.dialect", "org.hibernate.dialect.Oracle12cDialect");
        // Do not use hbm2ddl.auto=update/create in production; set to 'none' or remove
        props.put("hibernate.hbm2ddl.auto", "none");
        props.put("hibernate.show_sql", "false");

        return builder
                .dataSource(oracleDataSource)
                .packages("com.csio.hexagonal") // adjust to where your @Entity classes live
                .persistenceUnit("oraclePU")
                .properties(props)
                .build();
    }

    /**
     * Optional: expose an EntityManager bean bound to the oracleEntityManagerFactory.
     * This makes it straightforward to constructor-inject an EntityManager into adapters:
     *   public OracleJpaProcedureAdapter(@Qualifier("oracleEntityManager") EntityManager em) { ... }
     */
    @Bean(name = "oracleEntityManager")
    public EntityManager oracleEntityManager(@Qualifier("oracleEntityManagerFactory") EntityManagerFactory emf) {
        return emf.createEntityManager();
    }
}
