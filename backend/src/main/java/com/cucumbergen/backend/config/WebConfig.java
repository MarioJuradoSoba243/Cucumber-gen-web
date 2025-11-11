package com.cucumbergen.backend.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configures global CORS support for the REST API so the Vue frontend can interact with it.
 */
@Configuration
public class WebConfig {

    private static final String PROPERTY_ALLOWED_ORIGINS = "app.cors.allowed-origins";
    private static final String PROPERTY_ALLOWED_ORIGIN_PATTERNS = "app.cors.allowed-origin-patterns";
    private static final String DEFAULT_ALLOWED_ORIGINS = "http://localhost:5173,http://127.0.0.1:5173";

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
        List<String> allowedOriginPatterns = resolveAllowedOriginPatterns();
        if (!allowedOriginPatterns.isEmpty()) {
            configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        } else {
            configuration.setAllowedOrigins(resolveAllowedOrigins());
        }
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Registers the {@link CorsFilter} so preflight requests are processed before reaching
     * Spring MVC.
     *
     * @param corsConfigurationSource the configuration source used for CORS checks
     * @return filter registration bean with high precedence
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(CorsConfigurationSource corsConfigurationSource) {
        CorsFilter corsFilter = new CorsFilter(corsConfigurationSource);
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(corsFilter);
        registration.setOrder(-102);
        return registration;
    }

    private List<String> resolveAllowedOrigins() {
        return parseList(environment.getProperty(PROPERTY_ALLOWED_ORIGINS, DEFAULT_ALLOWED_ORIGINS));
    }

    private List<String> resolveAllowedOriginPatterns() {
        String configuredPatterns = environment.getProperty(PROPERTY_ALLOWED_ORIGIN_PATTERNS);
        if (configuredPatterns == null) {
            return List.of();
        }
        return parseList(configuredPatterns);
    }

    private List<String> parseList(String rawValue) {
        return Arrays.stream(rawValue.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());
    }
}
