package com.techknife.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Instant;

/**
 * Health indicator verifying application runtime startup state, uptime, and system thread status.
 */
@Slf4j
@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    @Value("${spring.application.name:tech-knife-backend}")
    private String applicationName;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public Health health() {
        try {
            long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
            int activeThreads = ManagementFactory.getThreadMXBean().getThreadCount();
            long freeMemoryMb = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long totalMemoryMb = Runtime.getRuntime().totalMemory() / (1024 * 1024);

            return Health.up()
                    .withDetail("application", applicationName)
                    .withDetail("status", "UP")
                    .withDetail("activeProfile", activeProfile)
                    .withDetail("uptimeMs", uptimeMs)
                    .withDetail("uptimeSeconds", uptimeMs / 1000)
                    .withDetail("activeThreads", activeThreads)
                    .withDetail("freeMemoryMb", freeMemoryMb)
                    .withDetail("totalMemoryMb", totalMemoryMb)
                    .withDetail("timestamp", Instant.now().toString())
                    .build();

        } catch (Exception ex) {
            log.error("Application health check failed: {}", ex.getMessage(), ex);
            return Health.down(ex)
                    .withDetail("application", applicationName)
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
