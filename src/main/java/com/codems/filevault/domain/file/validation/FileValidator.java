package com.codems.filevault.domain.file.validation;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void validate(MultipartFile file);
}
