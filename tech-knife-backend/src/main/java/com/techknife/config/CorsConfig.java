package com.techknife.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * CORS Configuration providing cross-origin access control rules based on CLIENT_URL environment variables.
 */
@Slf4j
@Configuration
public class CorsConfig {

    @Value("${client.url:http://localhost:5173,http://localhost:3000}")
    private String clientUrl;

    /**
     * Creates CorsConfigurationSource bean for Spring Security filtering.
     *
     * @return CorsConfigurationSource instance
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> allowedOrigins = Arrays.asList(clientUrl.split(","));
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                log.info("==== [CorsTrace] Evaluating CORS for Origin='{}', Method='{}', URI='{}' ====",
                        request.getHeader("Origin"), request.getMethod(), request.getRequestURI());
                return super.getCorsConfiguration(request);
            }
        };
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
