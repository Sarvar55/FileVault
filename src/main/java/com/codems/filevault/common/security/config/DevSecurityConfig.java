package com.codems.filevault.common.security.config;

import com.codems.filevault.common.security.filter.JwtAuthenticationFilter;
import com.codems.filevault.common.security.handler.CustomAccessDeniedHandler;
import com.codems.filevault.common.security.handler.CustomAuthenticationEntryPoint;
import com.codems.filevault.domain.user.entity.Role;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevSecurityConfig {

    @Qualifier("publicPaths")
    private final List<String> publicPaths;
    @Qualifier("userPaths")
    private final List<String> userPaths;

    private final CorsConfigurationSource corsConfigurationSource;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> {
                    publicPaths.forEach(path -> auth.requestMatchers(path).permitAll());
                    userPaths.forEach(path -> auth.requestMatchers(path).hasRole(Role.USER.name()));
                    auth.anyRequest().denyAll();
                })
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
