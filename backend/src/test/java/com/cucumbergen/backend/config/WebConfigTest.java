package com.cucumbergen.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Tests for {@link WebConfig}.
 */
class WebConfigTest {

    @Test
    void corsConfigurationSource_usesConfiguredOrigins() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("app.cors.allowed-origins", "http://localhost:5173, https://example.com");

        WebConfig config = new WebConfig(environment);
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/tests");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals(List.of("http://localhost:5173", "https://example.com"), configuration.getAllowedOrigins());
        assertTrue(configuration.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
        assertEquals(List.of("*"), configuration.getAllowedHeaders());
        assertTrue(Boolean.TRUE.equals(configuration.getAllowCredentials()));
    }
}
