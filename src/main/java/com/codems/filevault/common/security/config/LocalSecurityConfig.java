package com.codems.filevault.common.security.config;

import com.codems.filevault.common.security.filter.JwtAuthenticationFilter;
import com.codems.filevault.common.security.handler.CustomAccessDeniedHandler;
import com.codems.filevault.common.security.handler.CustomAuthenticationEntryPoint;
import com.codems.filevault.common.security.provider.LocalAuthenticationProvider;
import com.codems.filevault.domain.user.entity.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@Profile("local")
public class LocalSecurityConfig {

    private final LocalAuthenticationProvider localAuthenticationProvider;

    private final List<String> publicPaths;
    private final List<String> userPaths;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public LocalSecurityConfig(LocalAuthenticationProvider localAuthenticationProvider, @Qualifier("publicPaths") List<String> publicPaths, @Qualifier("userPaths") List<String> userPaths, CustomAccessDeniedHandler customAccessDeniedHandler, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.localAuthenticationProvider = localAuthenticationProvider;
        this.publicPaths = publicPaths;
        this.userPaths = userPaths;

        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                            publicPaths.forEach(path -> auth.requestMatchers(path).permitAll());
                            userPaths.forEach(path -> auth.requestMatchers(path).hasRole(Role.USER.name()));
                            auth.anyRequest().denyAll();
                        }
                )
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authenticationProvider(localAuthenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
