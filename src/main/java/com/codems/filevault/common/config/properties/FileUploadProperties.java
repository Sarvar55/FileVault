package com.codems.filevault.common.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.unit.DataSize;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    @NotNull
    private DataSize maxSize;
}
