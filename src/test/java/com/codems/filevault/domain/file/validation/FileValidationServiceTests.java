package com.codems.filevault.domain.file.validation;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.codems.filevault.common.config.properties.AppFileProperties;
import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

class FileValidationServiceTests {

    private FileValidationService validationService;

    @BeforeEach
    void setUp() {
        AppFileProperties properties = new AppFileProperties(
                Map.of(
                "jpg", "image/jpeg",
                "jpeg", "image/jpeg",
                "png", "image/png",
                "pdf", "application/pdf",
                "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                ),
                new AppFileProperties.Storage("./storage/test"),
                new AppFileProperties.Upload(DataSize.ofMegabytes(10)),
                new AppFileProperties.Cleanup(false, java.time.Duration.ofHours(1), java.time.Duration.ofHours(24))
        );
        FileMediaTypePolicy fileMediaTypePolicy = new FileMediaTypePolicy(properties);

        validationService = new FileValidationService(List.<FileValidator>of(
                new FilenameValidator(),
                new ExtensionValidator(fileMediaTypePolicy),
                new SizeValidator(properties),
                new ContentTypeValidator(fileMediaTypePolicy),
                new FileSignatureValidator(fileMediaTypePolicy)
        ));
    }

    @Test
    void acceptsFileWhenExtensionContentTypeAndSignatureMatch() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
        );

        assertThatCode(() -> validationService.validate(file)).doesNotThrowAnyException();
    }

    @Test
    void acceptsDocxWhenOoxmlSignatureMatches() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "document.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxContent()
        );

        assertThatCode(() -> validationService.validate(file)).doesNotThrowAnyException();
    }

    @Test
    void rejectsPathTraversalFilename() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../image.png", "image/png", new byte[]{1}
        );

        assertError(file, FileErrorType.INVALID_FILENAME);
    }

    @Test
    void rejectsDeclaredContentTypeThatDoesNotMatchExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "image/png", "%PDF-1.7".getBytes()
        );

        assertError(file, FileErrorType.INVALID_CONTENT_TYPE);
    }

    @Test
    void rejectsContentWhoseSignatureDoesNotMatchExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "plain text".getBytes()
        );

        assertError(file, FileErrorType.INVALID_FILE_SIGNATURE);
    }

    private void assertError(MockMultipartFile file, FileErrorType errorType) {
        assertThatThrownBy(() -> validationService.validate(file))
                .isInstanceOfSatisfying(BaseException.class,
                        exception -> org.assertj.core.api.Assertions.assertThat(exception.getCode())
                                .isEqualTo(errorType.code()));
    }

    private byte[] docxContent() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            addZipEntry(zipOutputStream, "[Content_Types].xml",
                    "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                            + "<Override PartName=\"/word/document.xml\" "
                            + "ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>"
                            + "</Types>");
            addZipEntry(zipOutputStream, "word/document.xml",
                    "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"/>");
            zipOutputStream.finish();
            return outputStream.toByteArray();
        }
    }

    private void addZipEntry(ZipOutputStream outputStream, String name, String content) throws IOException {
        outputStream.putNextEntry(new ZipEntry(name));
        outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        outputStream.closeEntry();
    }
}
