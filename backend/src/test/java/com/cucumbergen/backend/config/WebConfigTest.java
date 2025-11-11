package com.cucumbergen.backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Tests for {@link WebConfig}.
 */
class WebConfigTest {

    @Test
    void corsConfigurationSource_usesDefaultOriginsWhenNotConfigured() {
        MockEnvironment environment = new MockEnvironment();

        WebConfig config = new WebConfig(environment);
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/tests");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals(List.of("http://localhost:5173", "http://127.0.0.1:5173"), configuration.getAllowedOrigins());
        assertTrue(configuration.getAllowedMethods().containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
        assertEquals(List.of("*"), configuration.getAllowedHeaders());
        assertTrue(Boolean.TRUE.equals(configuration.getAllowCredentials()));
        assertEquals(3600L, configuration.getMaxAge());
    }

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
        assertEquals(3600L, configuration.getMaxAge());
    }

    @Test
    void corsConfigurationSource_supportsOriginPatterns() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("app.cors.allowed-origin-patterns", "http://*.example.com, https://*.example.org");

        WebConfig config = new WebConfig(environment);
        CorsConfigurationSource source = config.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/v1/tests");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertEquals(List.of("http://*.example.com", "https://*.example.org"), configuration.getAllowedOriginPatterns());
        assertTrue(configuration.getAllowedOrigins() == null || configuration.getAllowedOrigins().isEmpty());
    }

    @Test
    void corsFilter_isRegisteredWithHighPrecedence() {
        MockEnvironment environment = new MockEnvironment();

        WebConfig config = new WebConfig(environment);
        CorsConfigurationSource source = config.corsConfigurationSource();
        FilterRegistrationBean<CorsFilter> registration = config.corsFilter(source);

        assertEquals(-102, registration.getOrder());
        assertTrue(registration.getFilter() instanceof CorsFilter);
    }
}
