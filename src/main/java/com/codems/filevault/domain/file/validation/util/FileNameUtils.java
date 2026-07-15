package com.codems.filevault.domain.file.validation.util;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import java.util.Locale;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public final class FileNameUtils {

    private FileNameUtils() {
    }

    public static String originalFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw BaseException.of(FileErrorType.INVALID_FILENAME);
        }
        return filename;
    }

    public static String extension(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(originalFilename(file));
        if (extension == null || extension.isBlank()) {
            throw BaseException.of(FileErrorType.UNSUPPORTED_EXTENSION);
        }
        return extension.toLowerCase(Locale.ROOT);
    }
}
