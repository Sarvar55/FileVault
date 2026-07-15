package com.codems.filevault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
public class FileVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileVaultApplication.class, args);
    }

}
