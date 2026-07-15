package com.codems.filevault.common.config.properties;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "file.cleanup")
public class FileCleanupProperties {

    private boolean enabled;

    @NotNull
    private Duration fixedDelay;

    @NotNull
    private Duration orphanGracePeriod;
}
