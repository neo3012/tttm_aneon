package com.company.aneon.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

class CorsConfigTest {

    @Test
    void allowsConfiguredFrontendOriginsAndRequiredHeaders() {
        CorsConfigurationSource source = new CorsConfig().corsConfigurationSource(
                "http://localhost:3000, http://localhost:5173");
        CorsConfiguration configuration = source.getCorsConfiguration(
                new MockHttpServletRequest("OPTIONS", "/api/v1/f01/premises"));

        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactly("http://localhost:3000", "http://localhost:5173");
        assertThat(configuration.getAllowedMethods()).contains("GET", "POST", "PUT", "PATCH", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).contains("Authorization", "Idempotency-Key");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }
}
