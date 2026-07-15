package com.codems.filevault.common.exceptions.types;

import org.springframework.http.HttpStatus;

public interface ErrorType {

    String code();

    String message();

    HttpStatus status();
}
