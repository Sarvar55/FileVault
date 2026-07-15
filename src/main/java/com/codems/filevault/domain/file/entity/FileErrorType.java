package com.codems.filevault.domain.file.entity;

import com.codems.filevault.common.exceptions.types.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorType implements ErrorType {

    UNSUPPORTED_EXTENSION("FILE_002", "Unsupported file extension", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE("FILE_003", "File size exceeds the allowed limit", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_004", "File not found", HttpStatus.NOT_FOUND),
    STORAGE_FAILURE("FILE_005", "Could not store file", HttpStatus.INTERNAL_SERVER_ERROR),
    DOWNLOAD_FAILURE("FILE_006", "Could not read file", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILENAME("FILE_007", "Invalid filename", HttpStatus.BAD_REQUEST),
    INVALID_CONTENT_TYPE("FILE_008", "Invalid file content type", HttpStatus.BAD_REQUEST),
    INVALID_FILE_SIGNATURE("FILE_009", "File content does not match its extension", HttpStatus.BAD_REQUEST),
    FILE_VALIDATION_FAILURE("FILE_010", "Could not validate file content", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}
