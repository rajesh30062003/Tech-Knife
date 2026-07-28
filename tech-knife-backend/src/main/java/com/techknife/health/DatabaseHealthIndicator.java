package com.techknife.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Health indicator verifying MongoDB database connection and responsiveness.
 */
@Slf4j
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    public DatabaseHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();
        try {
            Document pingCommand = new Document("ping", 1);
            Document result = mongoTemplate.getDb().runCommand(pingCommand);
            long responseTimeMs = System.currentTimeMillis() - startTime;

            Object okObj = result.get("ok");
            double okVal = (okObj instanceof Number number) ? number.doubleValue() : 0.0;
            boolean isOk = (okVal == 1.0);

            if (isOk) {
                return Health.up()
                        .withDetail("database", mongoTemplate.getDb().getName())
                        .withDetail("type", "MongoDB")
                        .withDetail("pingResponseTimeMs", responseTimeMs)
                        .withDetail("status", "CONNECTED")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", mongoTemplate.getDb().getName())
                        .withDetail("error", "MongoDB ping command returned unexpected status: " + result.toJson())
                        .build();
            }

        } catch (Exception ex) {
            log.error("MongoDB database health check failed: {}", ex.getMessage());
            return Health.down(ex)
                    .withDetail("type", "MongoDB")
                    .withDetail("database", mongoTemplate != null && mongoTemplate.getDb() != null ? mongoTemplate.getDb().getName() : "UNKNOWN")
                    .withDetail("error", "Database connection error: " + ex.getMessage())
                    .build();
        }
    }
}
