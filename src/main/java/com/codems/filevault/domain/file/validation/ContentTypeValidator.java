package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import com.codems.filevault.domain.file.validation.util.FileNameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Order(4)
@RequiredArgsConstructor
public class ContentTypeValidator implements FileValidator {

    private final FileMediaTypePolicy fileMediaTypePolicy;

    @Override
    public void validate(MultipartFile file) {
        if (!fileMediaTypePolicy.matches(FileNameUtils.extension(file), file.getContentType())) {
            throw BaseException.of(FileErrorType.INVALID_CONTENT_TYPE);
        }
    }
}
