package com.techknife.backend.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.UuidRepresentation;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.techknife.backend.audit.AuditorAwareImpl;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

/**
 * Production-ready MongoDB Atlas Configuration class for backend module.
 * Configures connection pooling, SSL, read preference, write concern, retry policies, and startup validation.
 */
@Slf4j
@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
@EnableMongoRepositories(basePackages = "com.techknife")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:tkems_db}")
    private String databaseName;

    @Value("${spring.data.mongodb.min-pool-size:10}")
    private int minPoolSize;

    @Value("${spring.data.mongodb.max-pool-size:100}")
    private int maxPoolSize;

    @Value("${spring.data.mongodb.max-idle-time-ms:600000}")
    private long maxIdleTimeMs;

    @Value("${spring.data.mongodb.connect-timeout-ms:10000}")
    private int connectTimeoutMs;

    @Value("${spring.data.mongodb.socket-timeout-ms:30000}")
    private int socketTimeoutMs;

    @Value("${spring.data.mongodb.server-selection-timeout-ms:30000}")
    private long serverSelectionTimeoutMs;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    /**
     * Validates MongoDB configuration at startup.
     */
    @PostConstruct
    public void validateConfiguration() {
        log.info("Initializing MongoDB Atlas connection configuration...");
        if (mongoUri == null || mongoUri.trim().isEmpty() || mongoUri.contains("YOUR_MONGODB_URI")) {
            String errorMsg = "CRITICAL CONFIGURATION ERROR: MONGODB_URI environment variable is missing or invalid! " +
                    "Please specify MONGODB_URI in your environment or active spring profile.";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        if (databaseName == null || databaseName.trim().isEmpty()) {
            String errorMsg = "CRITICAL CONFIGURATION ERROR: MONGODB_DATABASE property is missing or blank!";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }

        log.info("MongoDB Atlas Configuration validated successfully. Target Database: '{}'", databaseName);
    }

    @Override
    @Bean
    @Primary
    public MongoClient mongoClient() {
        log.info("Configuring MongoDB Atlas MongoClient with SSL and Connection Pooling...");

        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .readPreference(ReadPreference.primaryPreferred())
                .writeConcern(WriteConcern.MAJORITY.withJournal(true))
                .retryWrites(true)
                .retryReads(true)
                .applyToConnectionPoolSettings(builder -> builder
                        .minSize(minPoolSize)
                        .maxSize(maxPoolSize)
                        .maxConnectionIdleTime(maxIdleTimeMs, TimeUnit.MILLISECONDS)
                )
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(connectTimeoutMs, TimeUnit.MILLISECONDS)
                        .readTimeout(socketTimeoutMs, TimeUnit.MILLISECONDS)
                )
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(serverSelectionTimeoutMs, TimeUnit.MILLISECONDS)
                )
                .applyToSslSettings(builder -> builder.enabled(true))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseName());
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
