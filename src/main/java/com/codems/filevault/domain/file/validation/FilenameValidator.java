package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import com.codems.filevault.domain.file.validation.util.FileNameUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Order(1)
public class FilenameValidator implements FileValidator {

    private static final int MAX_FILENAME_LENGTH = 255;

    @Override
    public void validate(MultipartFile file) {
        String originalFilename = FileNameUtils.originalFilename(file);
        String cleanedFilename = StringUtils.cleanPath(originalFilename);

        if (originalFilename.contains("..")
                || cleanedFilename.contains("/")
                || cleanedFilename.contains("\\")
                || cleanedFilename.length() > MAX_FILENAME_LENGTH) {
            throw BaseException.of(FileErrorType.INVALID_FILENAME);
        }
    }
}
