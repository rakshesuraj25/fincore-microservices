package com.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.Security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(
            JwtAuthFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
            // Disable CSRF because this is a stateless REST API
            .csrf()
            .disable()

            // Configure authorization
            .authorizeRequests()

            // Public endpoints
            .antMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",

                    "/api/v1/registeruser",
                    "/api/v1/auth/registeruser",

                    "/api/v1/loginuser",
                    "/api/v1/auth/loginuser"
            )
            .permitAll()

            // All other endpoints require authentication
            .anyRequest()
            .authenticated()

            // Stateless JWT authentication
            .and()
            .sessionManagement()
            .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
            )

            // Authentication provider
            .and()
            .authenticationProvider(
                    authenticationProvider
            )

            // JWT filter runs before Spring's username/password filter
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );
    }
}