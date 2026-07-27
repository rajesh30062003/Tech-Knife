package com.techknife.config;

import com.techknife.interceptor.RequestContextInterceptor;
import com.techknife.interceptor.RequestTimingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC Configuration registering interceptors and static path exclusions.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestTimingInterceptor requestTimingInterceptor;
    private final RequestContextInterceptor requestContextInterceptor;

    private static final String[] EXCLUDE_PATH_PATTERNS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**",
            "/favicon.ico",
            "/*.css",
            "/*.js",
            "/*.png",
            "/*.jpg",
            "/*.svg"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestContextInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH_PATTERNS)
                .order(1);

        registry.addInterceptor(requestTimingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH_PATTERNS)
                .order(2);
    }
}
