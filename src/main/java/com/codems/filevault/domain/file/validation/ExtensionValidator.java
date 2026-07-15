package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import com.codems.filevault.domain.file.validation.util.FileNameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Order(2)
@RequiredArgsConstructor
public class ExtensionValidator implements FileValidator {

    private final FileMediaTypePolicy fileMediaTypePolicy;

    @Override
    public void validate(MultipartFile file) {
        String extension = FileNameUtils.extension(file);
        if (!fileMediaTypePolicy.supports(extension)) {
            throw BaseException.of(FileErrorType.UNSUPPORTED_EXTENSION);
        }
    }
}
