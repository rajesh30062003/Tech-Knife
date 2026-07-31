package com.techknife.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.techknife.backend.audit.AuditorAwareImpl;
import com.techknife.project.converter.ProjectStatusReadingConverter;
import com.techknife.project.converter.ProjectStatusWritingConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMongoAuditing(auditorAwareRef = "auditorProvider")
@EnableMongoRepositories(basePackages = "com.techknife")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Value("${spring.data.mongodb.database:techknife}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean
    @Primary
    public MongoClient mongoClient() {
        log.info("Connecting to MongoDB Atlas...");
        
        ConnectionString connString = new ConnectionString(connectionString);
        
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .applyToSocketSettings(builder -> builder
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS))
                .applyToClusterSettings(builder -> builder
                        .serverSelectionTimeout(15, TimeUnit.SECONDS))
                .applyToConnectionPoolSettings(builder -> builder
                        .maxSize(50)
                        .minSize(5)
                        .maxWaitTime(10, TimeUnit.SECONDS))
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
    @Primary
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Override
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(
                new ProjectStatusReadingConverter(),
                new ProjectStatusWritingConverter(),
                new GenderReadingConverter(),
                new BackendGenderReadingConverter(),
                new EntityGenderReadingConverter(),
                new BloodGroupReadingConverter(),
                new MaritalStatusReadingConverter(),
                new EmploymentTypeReadingConverter(),
                new BackendEmploymentTypeReadingConverter(),
                new BackendEmployeeStatusReadingConverter(),
                new InternStatusReadingConverter(),
                new AddressReadingConverter(),
                new LocalDateReadingConverter()
        ));
    }

    @ReadingConverter
    public static class InternStatusReadingConverter implements Converter<String, com.techknife.intern.entity.InternStatus> {
        @Override
        public com.techknife.intern.entity.InternStatus convert(String source) {
            return com.techknife.intern.entity.InternStatus.fromString(source);
        }
    }

    @ReadingConverter
    public static class LocalDateReadingConverter implements Converter<String, java.time.LocalDate> {
        @Override
        public java.time.LocalDate convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            try {
                return java.time.LocalDate.parse(source);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @ReadingConverter
    public static class AddressReadingConverter implements Converter<String, com.techknife.employee.entity.Address> {
        @Override
        public com.techknife.employee.entity.Address convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }
            return com.techknife.employee.entity.Address.builder()
                    .street(source)
                    .build();
        }
    }

    @ReadingConverter
    public static class GenderReadingConverter implements Converter<String, com.techknife.employee.entity.Gender> {
        @Override
        public com.techknife.employee.entity.Gender convert(String source) {
            return com.techknife.employee.entity.Gender.fromString(source);
        }
    }

    @ReadingConverter
    public static class BackendGenderReadingConverter implements Converter<String, com.techknife.backend.entity.Employee.Gender> {
        @Override
        public com.techknife.backend.entity.Employee.Gender convert(String source) {
            return com.techknife.backend.entity.Employee.Gender.fromString(source);
        }
    }

    @ReadingConverter
    public static class EntityGenderReadingConverter implements Converter<String, com.techknife.entity.Employee.Gender> {
        @Override
        public com.techknife.entity.Employee.Gender convert(String source) {
            return com.techknife.entity.Employee.Gender.fromString(source);
        }
    }

    @ReadingConverter
    public static class BloodGroupReadingConverter implements Converter<String, com.techknife.employee.entity.BloodGroup> {
        @Override
        public com.techknife.employee.entity.BloodGroup convert(String source) {
            return com.techknife.employee.entity.BloodGroup.fromString(source);
        }
    }

    @ReadingConverter
    public static class MaritalStatusReadingConverter implements Converter<String, com.techknife.employee.entity.MaritalStatus> {
        @Override
        public com.techknife.employee.entity.MaritalStatus convert(String source) {
            return com.techknife.employee.entity.MaritalStatus.fromString(source);
        }
    }

    @ReadingConverter
    public static class EmploymentTypeReadingConverter implements Converter<String, com.techknife.employee.entity.EmploymentType> {
        @Override
        public com.techknife.employee.entity.EmploymentType convert(String source) {
            return com.techknife.employee.entity.EmploymentType.fromString(source);
        }
    }

    @ReadingConverter
    public static class BackendEmploymentTypeReadingConverter implements Converter<String, com.techknife.backend.entity.Employee.EmploymentType> {
        @Override
        public com.techknife.backend.entity.Employee.EmploymentType convert(String source) {
            return com.techknife.backend.entity.Employee.EmploymentType.fromString(source);
        }
    }

    @ReadingConverter
    public static class BackendEmployeeStatusReadingConverter implements Converter<String, com.techknife.backend.entity.Employee.EmployeeStatus> {
        @Override
        public com.techknife.backend.entity.Employee.EmployeeStatus convert(String source) {
            return com.techknife.backend.entity.Employee.EmployeeStatus.fromString(source);
        }
    }
}
