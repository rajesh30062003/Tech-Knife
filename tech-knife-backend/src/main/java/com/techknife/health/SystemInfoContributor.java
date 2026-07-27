package com.techknife.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Actuator InfoContributor exposing system, runtime environment, memory, and application metadata.
 */
@Component
public class SystemInfoContributor implements InfoContributor {

    @Value("${spring.application.name:tech-knife-backend}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> appDetails = new HashMap<>();
        appDetails.put("name", applicationName);
        appDetails.put("version", appVersion);
        appDetails.put("buildTime", Instant.now().toString());
        appDetails.put("activeProfile", activeProfile);
        appDetails.put("springBootVersion", SpringBootVersion.getVersion());

        Map<String, Object> systemDetails = new HashMap<>();
        systemDetails.put("javaVersion", System.getProperty("java.version"));
        systemDetails.put("javaVendor", System.getProperty("java.vendor"));
        systemDetails.put("operatingSystem", System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
        systemDetails.put("availableProcessors", Runtime.getRuntime().availableProcessors());

        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long freeMemory = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;

        Map<String, Object> memoryDetails = new HashMap<>();
        memoryDetails.put("maxMemoryMb", maxMemory);
        memoryDetails.put("totalMemoryMb", totalMemory);
        memoryDetails.put("freeMemoryMb", freeMemory);
        memoryDetails.put("usedMemoryMb", usedMemory);

        systemDetails.put("memory", memoryDetails);
        systemDetails.put("uptimeMs", ManagementFactory.getRuntimeMXBean().getUptime());

        builder.withDetail("app", appDetails)
               .withDetail("system", systemDetails);
    }
}
