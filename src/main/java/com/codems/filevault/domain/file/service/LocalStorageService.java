package com.codems.filevault.domain.file.service;

import com.codems.filevault.common.config.properties.AppFileProperties;
import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.file.entity.FileErrorType;
import com.codems.filevault.domain.file.dto.StoredFile;
import com.codems.filevault.domain.file.validation.util.FileNameUtils;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private static final String SHA_256 = "SHA-256";

    private final AppFileProperties properties;

    @PostConstruct
    void createStorageRoot() throws IOException {
        Files.createDirectories(rootPath());
    }

    @Override
    public StoredFile store(MultipartFile file) {
        String originalFilename = FileNameUtils.originalFilename(file);
        String extension = FileNameUtils.extension(file);
        String storedFilename = UUID.randomUUID() + "." + extension;
        Path target = resolve(storedFilename);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance(SHA_256);
            try (InputStream inputStream = file.getInputStream();
                 DigestInputStream digestInputStream = new DigestInputStream(inputStream, messageDigest)) {
                Files.copy(digestInputStream, target);
            }

            return new StoredFile(
                    originalFilename,
                    storedFilename,
                    file.getContentType(),
                    extension,
                    file.getSize(),
                    HexFormat.of().formatHex(messageDigest.digest()),
                    target.toString()
            );
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw BaseException.of(FileErrorType.STORAGE_FAILURE);
        }
    }

    @Override
    public Resource load(String storedFilename) {
        try {
            Resource resource = new UrlResource(resolve(storedFilename).toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw BaseException.of(FileErrorType.FILE_NOT_FOUND);
            }
            return resource;
        } catch (IOException exception) {
            throw BaseException.of(FileErrorType.DOWNLOAD_FAILURE);
        }
    }

    @Override
    public void delete(String storedFilename) {
        try {
            Files.deleteIfExists(resolve(storedFilename));
        } catch (IOException exception) {
            throw BaseException.of(FileErrorType.STORAGE_FAILURE);
        }
    }

    @Override
    public boolean exists(String storedFilename) {
        return Files.isRegularFile(resolve(storedFilename));
    }

    @Override
    public Set<String> findStoredFilenamesModifiedBefore(Instant threshold) {
        try (Stream<Path> paths = Files.list(rootPath())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> lastModifiedInstant(path).isBefore(threshold))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
        } catch (IOException exception) {
            throw BaseException.of(FileErrorType.STORAGE_FAILURE);
        }
    }

    private Path resolve(String storedFilename) {
        Path path = rootPath().resolve(storedFilename).normalize();
        if (!path.startsWith(rootPath())) {
            throw BaseException.of(FileErrorType.STORAGE_FAILURE);
        }
        return path;
    }

    private Path rootPath() {
        return Path.of(properties.storage().rootPath()).toAbsolutePath().normalize();
    }

    private Instant lastModifiedInstant(Path path) {
        try {
            return Files.getLastModifiedTime(path).toInstant();
        } catch (IOException exception) {
            throw BaseException.of(FileErrorType.STORAGE_FAILURE);
        }
    }
}
