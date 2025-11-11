package com.cucumbergen.backend.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

class DataDirectoryConfigTest {

    @Test
    void shouldCreateParentDirectoryForSqliteDatabase() throws IOException {
        Path tempDir = Files.createTempDirectory("sqlite-db");
        Path databaseFile = tempDir.resolve("nested").resolve("app.db");

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.datasource.url", "jdbc:sqlite:" + databaseFile);

        DataDirectoryConfig config = new DataDirectoryConfig(environment);
        config.initializeDataDirectory();

        assertTrue(Files.exists(databaseFile.getParent()), "Expected SQLite parent directory to be created");
    }

    @Test
    void shouldIgnoreNonSqliteUrls() throws IOException {
        Path tempDir = Files.createTempDirectory("sqlite-db");
        Path nestedDirectory = tempDir.resolve("should-not-exist");

        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/test");

        DataDirectoryConfig config = new DataDirectoryConfig(environment);
        config.initializeDataDirectory();

        assertTrue(Files.notExists(nestedDirectory), "Non-SQLite URLs must not create directories");
    }
}
