package com.techknife.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Spring configuration providing Cloudinary SDK client initialization bean.
 */
@Slf4j
@Configuration
public class CloudinaryConfig {

    @Value("${app.cloudinary.cloud-name:tech-knife-cloud}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:123456789012345}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:cloudinary_secret_key_here}")
    private String apiSecret;

    /**
     * Initializes and configures the Cloudinary Java SDK client bean.
     *
     * @return Cloudinary instance
     */
    @Bean
    public Cloudinary cloudinary() {
        log.info("Initializing Cloudinary storage bean with cloud_name: '{}'", cloudName);

        Map<String, Object> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        );

        return new Cloudinary(config);
    }
}
