package com.codems.filevault.domain.file.validation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileValidationService {

    private final List<FileValidator> validators;

    public void validate(MultipartFile file) {
        validators.forEach(validator -> validator.validate(file));
    }
}
