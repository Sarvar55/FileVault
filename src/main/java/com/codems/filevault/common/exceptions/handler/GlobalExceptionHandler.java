package com.codems.filevault.common.exceptions.handler;


import java.util.LinkedHashMap;
import java.util.Map;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.common.exceptions.types.CommonErrorType;
import com.codems.filevault.domain.base.BaseResponse;
import com.codems.filevault.domain.file.entity.FileErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<Object>> handleBaseException(BaseException exception) {
        log.warn("Handled application exception [{}]: {}", exception.getCode(), exception.getMessage());
        return build(
                exception.getCode(),
                exception.getStatus(),
                exception.getMessage(),
                exception.getValidationErrors(),
                exception.getDetails()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validation failed with {} field errors", fieldErrors.size());

        return build(CommonErrorType.VALIDATION_FAILED, fieldErrors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse<Object>> handleMaxUploadSize(MaxUploadSizeExceededException exception) {
        log.warn("Multipart upload exceeded the configured request limit");
        return build(
                FileErrorType.FILE_TOO_LARGE.code(),
                FileErrorType.FILE_TOO_LARGE.status(),
                FileErrorType.FILE_TOO_LARGE.message(),
                null,
                null
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<Object>> handleAuthentication(AuthenticationException exception) {
        log.debug("Authentication exception handled: {}", exception.getMessage());
        return build(CommonErrorType.UNAUTHORIZED, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException exception) {
        log.warn("Bad credentials handled");
        return build(CommonErrorType.UNAUTHORIZED, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDenied(AccessDeniedException exception) {
        log.debug("Access denied handled");
        return build(CommonErrorType.FORBIDDEN, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleUnexpected(Exception exception) {
        log.error("Unexpected error", exception);
        return build(CommonErrorType.INTERNAL_ERROR, null);
    }

    private ResponseEntity<BaseResponse<Object>> build(CommonErrorType errorType, Map<String, String> fieldErrors) {
        return build(errorType.code(), errorType.status(), errorType.message(), fieldErrors, null);
    }

    private ResponseEntity<BaseResponse<Object>> build(
            String code,
            HttpStatus status,
            String message,
            Map<String, String> fieldErrors,
            Map<String, Object> details
    ) {
        return ResponseEntity.status(status)
                .body(BaseResponse.error(code, message, status, fieldErrors, details));
    }
}
