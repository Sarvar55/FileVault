package com.codems.filevault.domain.file.dto;

import org.springframework.core.io.Resource;

public record FileDownload(
        Resource resource,
        String originalFilename,
        String contentType,
        Long sizeBytes
) {
}
