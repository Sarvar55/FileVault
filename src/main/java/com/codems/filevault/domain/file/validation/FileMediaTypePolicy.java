package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.config.properties.AppFileProperties;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileMediaTypePolicy {

    private final AppFileProperties properties;

    public boolean supports(String extension) {
        return properties.allowedTypes().keySet().stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(extension::equals);
    }

    public boolean matches(String extension, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }
        String normalizedContentType = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return properties.allowedTypes().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(extension))
                .map(entry -> entry.getValue().trim().toLowerCase(Locale.ROOT))
                .anyMatch(normalizedContentType::equals);
    }
}
