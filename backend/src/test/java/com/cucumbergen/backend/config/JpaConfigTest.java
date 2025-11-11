package com.cucumbergen.backend.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Tests for {@link JpaConfig}.
 */
class JpaConfigTest {

    @Test
    void entityManagerFactory_scansBackendPackage() {
        Environment environment = new MockEnvironment();
        JpaConfig jpaConfig = new JpaConfig(environment);
        DataSource dataSource = Mockito.mock(DataSource.class);

        LocalContainerEntityManagerFactoryBean factoryBean =
                jpaConfig.entityManagerFactory(dataSource);

        DefaultPersistenceUnitManager persistenceUnitManager =
                (DefaultPersistenceUnitManager) ReflectionTestUtils.getField(factoryBean, "internalPersistenceUnitManager");

        String[] packagesToScan = (String[]) ReflectionTestUtils.getField(persistenceUnitManager, "packagesToScan");

        assertArrayEquals(new String[] {"com.cucumbergen.backend"},
                packagesToScan,
                "JpaConfig should scan the full backend package to pick converters");

        assertEquals("com.cucumbergen.backend.persistence.SQLitePlatform",
                factoryBean.getJpaPropertyMap().get("eclipselink.target-database"),
                "Default target database should point to the custom SQLite platform");
    }

    @Test
    void entityManagerFactory_normalizesLegacySqlitePlatformNames() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty(
                "spring.jpa.properties.eclipselink.target-database",
                "org.eclipse.persistence.platform.database.SQLitePlatform");
        JpaConfig jpaConfig = new JpaConfig(environment);
        DataSource dataSource = Mockito.mock(DataSource.class);

        LocalContainerEntityManagerFactoryBean factoryBean =
                jpaConfig.entityManagerFactory(dataSource);

        assertEquals("com.cucumbergen.backend.persistence.SQLitePlatform",
                factoryBean.getJpaPropertyMap().get("eclipselink.target-database"),
                "Legacy EclipseLink SQLite platform alias should map to the bundled implementation");
    }
}
