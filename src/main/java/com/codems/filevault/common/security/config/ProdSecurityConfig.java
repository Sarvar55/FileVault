package com.codems.filevault.common.security.config;

import com.codems.filevault.common.security.filter.JwtAuthenticationFilter;
import com.codems.filevault.common.security.handler.CustomAccessDeniedHandler;
import com.codems.filevault.common.security.handler.CustomAuthenticationEntryPoint;
import com.codems.filevault.domain.user.entity.Role;
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

import java.util.List;

@Configuration
@Profile("prod")
public class ProdSecurityConfig {

    private final List<String> publicPaths;
    private final List<String> userPaths;
    private final List<String> adminPaths;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public ProdSecurityConfig(@Qualifier("publicPaths") List<String> publicPaths, @Qualifier("userPaths") List<String> userPaths, @Qualifier("adminPaths") List<String> adminPaths, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.publicPaths = publicPaths;
        this.userPaths = userPaths;
        this.adminPaths = adminPaths;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .redirectToHttps(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                            publicPaths.forEach(path -> auth.requestMatchers(path).permitAll());
                            userPaths.forEach(path -> auth.requestMatchers(path).hasRole(Role.USER.name()));
                            adminPaths.forEach(path -> auth.requestMatchers(path).hasRole(Role.ADMIN.name()));
                            auth.anyRequest().denyAll();
                        }
                )
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }
}
