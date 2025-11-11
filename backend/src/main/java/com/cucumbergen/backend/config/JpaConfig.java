package com.cucumbergen.backend.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configures JPA using EclipseLink as the persistence provider.
 */
@Configuration
@EnableTransactionManagement
public class JpaConfig {

    private final Environment environment;

    public JpaConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("com.cucumbergen.backend");
        factoryBean.setPersistenceUnitName("cucumbergen");

        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        vendorAdapter.setShowSql(environment.getProperty("spring.jpa.show-sql", Boolean.class, Boolean.FALSE));
        String databasePlatform = environment.getProperty("spring.jpa.properties.eclipselink.target-database");
        if (databasePlatform == null || databasePlatform.isBlank()) {
            vendorAdapter.setDatabasePlatform("org.eclipse.persistence.platform.database.SQLitePlatform");
        } else {
            vendorAdapter.setDatabasePlatform(databasePlatform);
        }
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaDialect(new EclipseLinkJpaDialect());

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("eclipselink.weaving",
                environment.getProperty("spring.jpa.properties.eclipselink.weaving", "false"));
        jpaProperties.put("eclipselink.logging.level",
                environment.getProperty("spring.jpa.properties.eclipselink.logging.level", "INFO"));
        jpaProperties.put("jakarta.persistence.schema-generation.database.action",
                environment.getProperty("spring.jpa.properties.jakarta.persistence.schema-generation.database.action",
                        "none"));
        factoryBean.setJpaPropertyMap(jpaProperties);
        return factoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
