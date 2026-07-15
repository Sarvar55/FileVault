package com.codems.filevault.domain.auth;

import com.codems.filevault.common.exceptions.types.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorType implements ErrorType {

    EMAIL_ALREADY_EXISTS("AUTH_001", "Email already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS("AUTH_002", "Username already exists", HttpStatus.CONFLICT),
    COMPROMISED_PASSWORD("AUTH_003", "Password has been compromised", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("AUTH_004", "Invalid authentication token", HttpStatus.UNAUTHORIZED);

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
