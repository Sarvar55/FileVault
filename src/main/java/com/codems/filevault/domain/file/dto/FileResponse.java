package com.codems.filevault.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Uploaded file metadata")
public record FileResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Passport scan")
        String title,

        @Schema(example = "Personal document")
        String description,

        @Schema(example = "identity")
        String category,

        @Schema(example = "passport.pdf")
        String originalFilename,

        @Schema(example = "application/pdf")
        String contentType,

        @Schema(example = "pdf")
        String extension,

        @Schema(example = "1048576")
        Long sizeBytes,

        @Schema(example = "e3b0c44298fc1c149afbf4c8996fb924...")
        String sha256Checksum,

        LocalDateTime createdAt
) {
}
