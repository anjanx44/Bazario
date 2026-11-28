package com.bazario.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards the actuator configuration split that the review on PR #38 asked for:
 * safe defaults live in application.yml, and only the dev profile adds Prometheus.
 */
class ActuatorConfigTest {

    private static PropertySource<?> load(String file) throws IOException {
        List<PropertySource<?>> sources =
                new YamlPropertySourceLoader().load(file, new ClassPathResource(file));
        assertThat(sources).isNotEmpty();
        return sources.get(0);
    }

    @Test
    void commonConfigIsSafeForEveryProfile() throws IOException {
        PropertySource<?> base = load("application.yml");

        // Prometheus is opt-in, so a deployment that never sets the profile stays closed.
        String exposure = String.valueOf(base.getProperty("management.endpoints.web.exposure.include"));
        assertThat(exposure).contains("health,info");
        assertThat(exposure).doesNotContain("prometheus");

        // The probe endpoint is anonymous: internal details must never leak through it.
        assertThat(base.getProperty("management.endpoint.health.show-details")).isEqualTo("never");
        assertThat(base.getProperty("management.endpoint.health.group.liveness.show-details")).isEqualTo("never");
        assertThat(base.getProperty("management.endpoint.health.group.readiness.show-details")).isEqualTo("never");

        // Probes exist for every profile, otherwise readiness would stop checking the DB.
        assertThat(base.getProperty("management.endpoint.health.probes.enabled")).isEqualTo(Boolean.TRUE);
        assertThat(String.valueOf(base.getProperty("management.endpoint.health.group.liveness.include")))
                .isEqualTo("livenessState,ping");
        assertThat(String.valueOf(base.getProperty("management.endpoint.health.group.readiness.include")))
                .isEqualTo("readinessState,db");
    }

    @Test
    void devProfileOnlyAddsPrometheus() throws IOException {
        PropertySource<?> dev = load("application-dev.yml");

        assertThat(String.valueOf(dev.getProperty("management.endpoints.web.exposure.include")))
                .isEqualTo("health,info,prometheus");

        // Everything else stays in the common file, so the two files cannot drift apart.
        assertThat(dev.getProperty("management.endpoint.health.show-details")).isNull();
        assertThat(dev.getProperty("management.endpoint.health.group.readiness.include")).isNull();
    }
}
