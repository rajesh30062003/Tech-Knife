package com.techknife.health;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Health indicator verifying Cloudinary cloud storage SDK configuration and readiness.
 */
@Slf4j
@Component
public class StorageHealthIndicator implements HealthIndicator {

    @Autowired(required = false)
    private Cloudinary cloudinary;

    @Value("${app.cloudinary.cloud-name:tech-knife-cloud}")
    private String cloudName;

    @Override
    public Health health() {
        try {
            if (cloudinary != null && cloudinary.config != null && StringUtils.hasText(cloudinary.config.cloudName)) {
                return Health.up()
                        .withDetail("provider", "Cloudinary")
                        .withDetail("cloudName", cloudinary.config.cloudName)
                        .withDetail("secure", cloudinary.config.secure)
                        .withDetail("status", "CONFIGURED")
                        .build();
            } else if (StringUtils.hasText(cloudName)) {
                return Health.up()
                        .withDetail("provider", "Cloudinary")
                        .withDetail("cloudName", cloudName)
                        .withDetail("status", "CONFIGURED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("provider", "Cloudinary")
                        .withDetail("error", "Cloudinary credentials not properly configured")
                        .build();
            }

        } catch (Exception ex) {
            log.error("Storage health check failed: {}", ex.getMessage());
            return Health.down(ex)
                    .withDetail("provider", "Cloudinary")
                    .withDetail("error", "Cloudinary storage error: " + ex.getMessage())
                    .build();
        }
    }
}
