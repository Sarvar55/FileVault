package com.codems.filevault.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "Multipart file upload request")
public record FileUploadMultipartRequest(
        @Schema(description = "File metadata")
        FileUploadRequest request,

        @Schema(description = "Physical file", type = "string", format = "binary")
        MultipartFile file
) {
}
