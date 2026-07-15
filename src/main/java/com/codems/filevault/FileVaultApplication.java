package com.codems.filevault;

import com.codems.filevault.common.config.properties.AppFileProperties;
import com.codems.filevault.common.config.properties.CorsConfigProperties;
import com.codems.filevault.common.config.properties.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
        AppFileProperties.class,
        CorsConfigProperties.class,
        JwtProperties.class
})
@EnableScheduling
public class FileVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileVaultApplication.class, args);
    }

}
