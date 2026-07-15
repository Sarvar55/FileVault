package com.codems.filevault.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "File metadata submitted with multipart upload")
public record FileUploadRequest(
        @Schema(example = "Passport scan")
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be at most 150 characters")
        String title,

        @Schema(example = "Personal document")
        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @Schema(example = "identity")
        @NotBlank(message = "Category is required")
        @Size(max = 100, message = "Category must be at most 100 characters")
        String category
) {
}
