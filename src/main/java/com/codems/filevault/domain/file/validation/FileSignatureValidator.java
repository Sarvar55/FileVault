package com.codems.filevault.domain.file.validation;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import java.io.IOException;
import java.io.InputStream;

import com.codems.filevault.domain.file.validation.util.FileNameUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

@Component
@Order(5)
@RequiredArgsConstructor
public class FileSignatureValidator implements FileValidator {

    private final Tika tika = new Tika();
    private final FileMediaTypePolicy fileMediaTypePolicy;

    @Override
    public void validate(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             TikaInputStream tikaInputStream = TikaInputStream.get(inputStream)) {
            String detectedContentType = tika.detect(tikaInputStream);
            if (!fileMediaTypePolicy.matches(FileNameUtils.extension(file), detectedContentType)) {
                throw BaseException.of(FileErrorType.INVALID_FILE_SIGNATURE);
            }
        } catch (IOException exception) {
            throw BaseException.of(FileErrorType.FILE_VALIDATION_FAILURE);
        }
    }
}
