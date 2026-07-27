package com.techknife.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password encoder configuration providing BCrypt password hashing bean with strength 12.
 */
@Configuration
public class PasswordConfig {

    /**
     * Reusable PasswordEncoder bean using BCrypt hashing with strength 12.
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
