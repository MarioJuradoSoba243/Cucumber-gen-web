package com.cucumbergen.backend.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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
    }
}
