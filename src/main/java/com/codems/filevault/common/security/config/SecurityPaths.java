package com.codems.filevault.common.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SecurityPaths {

    @Bean("publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/auth/register",
                "/api/auth/login",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/h2-console",
                "/h2-console/**"
        );
    }

    @Bean("userPaths")
    public List<String> userPaths() {
        return List.of(
                "/api/files/**"
        );
    }
}
