package com.codems.filevault.domain.file.service;

import com.codems.filevault.domain.file.dto.StoredFile;
import java.time.Instant;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFile store(MultipartFile file);

    Resource load(String storedFilename);

    void delete(String storedFilename);

    boolean exists(String storedFilename);

    Set<String> findStoredFilenamesModifiedBefore(Instant threshold);
}
