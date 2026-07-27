package com.techknife.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot Actuator and Micrometer metrics configuration bean.
 */
@Slf4j
@Configuration
public class ActuatorConfig {

    @Value("${spring.application.name:tech-knife-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * Customizes Micrometer MeterRegistry with common tags for production metrics tracking.
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        log.info("Configuring Micrometer metrics common tags for application '{}' in profile '{}'",
                applicationName, activeProfile);
        return registry -> registry.config()
                .commonTags("application", applicationName, "environment", activeProfile);
    }
}
