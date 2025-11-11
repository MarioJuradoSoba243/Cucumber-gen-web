package com.cucumbergen.backend.config;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Ensures that the SQLite database directory exists before Flyway attempts to create a connection.
 */
@Configuration
public class DataDirectoryConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataDirectoryConfig.class);
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static final String FILE_PREFIX = "file:";

    private final Environment environment;

    public DataDirectoryConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initializeDataDirectory() {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        if (datasourceUrl == null || !datasourceUrl.startsWith(SQLITE_PREFIX)) {
            return;
        }

        String pathSegment = datasourceUrl.substring(SQLITE_PREFIX.length());
        if (pathSegment.startsWith(FILE_PREFIX)) {
            pathSegment = pathSegment.substring(FILE_PREFIX.length());
        }

        Path databasePath = Paths.get(pathSegment).toAbsolutePath().normalize();
        Path parentDirectory = databasePath.getParent();
        if (parentDirectory == null) {
            return;
        }

        if (Files.exists(parentDirectory)) {
            return;
        }

        try {
            Files.createDirectories(parentDirectory);
            LOGGER.info("Created SQLite data directory at {}", parentDirectory);
        } catch (IOException exception) {
            LOGGER.warn("Failed to create SQLite data directory at {}", parentDirectory, exception);
        }
    }
}
