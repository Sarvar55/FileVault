package com.codems.filevault.domain.file.dto;

public record StoredFile(
        String originalFilename,
        String storedFilename,
        String contentType,
        String extension,
        Long sizeBytes,
        String sha256Checksum,
        String storagePath
) {
}
