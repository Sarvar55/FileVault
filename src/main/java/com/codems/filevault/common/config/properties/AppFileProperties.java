package com.codems.filevault.common.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.file")
public record AppFileProperties(
        @NotEmpty Map<String, String> allowedTypes,
        @Valid @NotNull Storage storage,
        @Valid @NotNull Upload upload,
        @Valid @NotNull Cleanup cleanup
) {

    public record Storage(@NotBlank String rootPath) {
    }

    public record Upload(@NotNull DataSize maxSize) {
    }

    public record Cleanup(
            boolean enabled,
            @NotNull Duration fixedDelay,
            @NotNull Duration orphanGracePeriod
    ) {
    }
}
