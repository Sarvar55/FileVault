package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.config.properties.AppFileProperties;
import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Order(3)
@RequiredArgsConstructor
public class SizeValidator implements FileValidator {

    private final AppFileProperties properties;

    @Override
    public void validate(MultipartFile file) {
        if (file.getSize() > properties.upload().maxSize().toBytes()) {
            throw BaseException.of(FileErrorType.FILE_TOO_LARGE);
        }
    }
}
