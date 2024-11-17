package com.example.demo.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for setting up HTTP security rules in the application.
 * <p>
 * This class configures the security settings for the web application, including disabling
 * CSRF protection and allowing all requests without authentication. It uses Spring Security's
 * {@link SecurityFilterChain} to define how requests should be authorized and what security features
 * should be applied.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the HTTP security for the application.
     * <p>
     * This method disables CSRF protection and allows all requests to pass through without requiring authentication.
     * </p>
     *
     * @param http the HttpSecurity object used to configure the HTTP security rules
     * @return a {@link SecurityFilterChain} object that defines the security filter chain for the application
     * @throws Exception if an error occurs while configuring HTTP security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfigurer -> csrfConfigurer.disable()) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Allow all requests without authentication
        return http.build();
    }
}
