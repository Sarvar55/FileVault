package com.codems.filevault.domain.file.controller;

import com.codems.filevault.common.constants.ApplicationConstants;
import com.codems.filevault.domain.base.BaseResponse;
import com.codems.filevault.domain.base.PageResponse;
import com.codems.filevault.domain.file.dto.FileDownload;
import com.codems.filevault.domain.file.dto.FileResponse;
import com.codems.filevault.domain.file.dto.FileUploadMultipartRequest;
import com.codems.filevault.domain.file.dto.FileUploadRequest;
import com.codems.filevault.domain.file.service.FileService;
import com.codems.filevault.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Secure file upload, listing, and download operations")
public class FileController {

    private final FileService fileService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            version = ApplicationConstants.DEFAULT_API_VERSION
    )
    @Operation(
            summary = "Upload file",
            description = "Uploads a file and stores its metadata for the authenticated user.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = FileUploadMultipartRequest.class),
                            encoding = @Encoding(
                                    name = "request",
                                    contentType = MediaType.APPLICATION_JSON_VALUE
                            )
                    )
            )
    )
    public ResponseEntity<BaseResponse<FileResponse>> upload(
            @AuthenticationPrincipal User user,
            @Valid @RequestPart("request") FileUploadRequest request,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(fileService.upload(user, request, file), HttpStatus.CREATED, "File uploaded successfully"));
    }

    @GetMapping(version = ApplicationConstants.DEFAULT_API_VERSION)
    @Operation(summary = "List own files", description = "Lists files owned by the authenticated user with pagination.")
    public BaseResponse<PageResponse<FileResponse>> listOwnFiles(
            @AuthenticationPrincipal User user,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable
    ) {
        return BaseResponse.success(fileService.listOwnFiles(user, pageable));
    }

    @GetMapping(value = "/{id}/download", version = ApplicationConstants.DEFAULT_API_VERSION)
    @Operation(summary = "Download file", description = "Downloads a file only if it belongs to the authenticated user.")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        FileDownload fileDownload = fileService.download(user, id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDownload.contentType()))
                .contentLength(fileDownload.sizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(fileDownload.originalFilename())
                        .build()
                        .toString())
                .body(fileDownload.resource());
    }

    @DeleteMapping(value = "/{id}", version = ApplicationConstants.DEFAULT_API_VERSION)
    @Operation(summary = "Delete file", description = "Soft-deletes owned file metadata. Physical content is removed by scheduled cleanup.")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        fileService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
