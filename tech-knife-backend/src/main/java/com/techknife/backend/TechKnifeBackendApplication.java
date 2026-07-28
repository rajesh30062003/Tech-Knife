package com.techknife.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
        scanBasePackages = "com.techknife",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableAsync
@EnableScheduling
public class TechKnifeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TechKnifeBackendApplication.class, args);
    }
}
