package com.cucumbergen.backend.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configures global CORS support for the REST API so the Vue frontend can interact with it.
 */
@Configuration
public class WebConfig {

    private static final String DEFAULT_ALLOWED_ORIGINS = "http://localhost:5173";

    private final Environment environment;

    public WebConfig(Environment environment) {
        this.environment = environment;
    }

    /**
     * Builds the {@link CorsConfigurationSource} registered by Spring MVC.
     *
     * @return configured CORS source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(resolveAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    private List<String> resolveAllowedOrigins() {
        String configuredOrigins = environment.getProperty("app.cors.allowed-origins", DEFAULT_ALLOWED_ORIGINS);
        return Arrays.stream(configuredOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .collect(Collectors.toList());
    }
}
