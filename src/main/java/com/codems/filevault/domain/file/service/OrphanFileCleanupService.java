package com.codems.filevault.domain.file.service;

import com.codems.filevault.common.config.properties.FileCleanupProperties;
import com.codems.filevault.domain.file.repository.FileMetadataRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "file.cleanup", name = "enabled", havingValue = "true")
public class OrphanFileCleanupService {

    private final StorageService storageService;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileCleanupProperties properties;

    @Scheduled(fixedDelayString = "${file.cleanup.fixed-delay}")
    public void cleanup() {
        deleteFilesOfSoftDeletedRecords();
        deleteOrphanFiles();
    }

    private void deleteFilesOfSoftDeletedRecords() {
        Set<String> deletedFilenames = fileMetadataRepository.findDeletedStoredFilenames();

        for (String filename : deletedFilenames) {
            if (storageService.exists(filename)) {
                deleteSafely(filename);
            }
        }
    }

    private void deleteOrphanFiles() {

        Instant threshold = Instant.now().minus(properties.getOrphanGracePeriod());
        Set<String> oldStoredFilenames = storageService.findStoredFilenamesModifiedBefore(threshold);
        if (oldStoredFilenames.isEmpty()) {
            return;
        }

        Set<String> activeFilenames = fileMetadataRepository.findExistingStoredFilenames(oldStoredFilenames);
        Set<String> orphanFilenames = new HashSet<>(oldStoredFilenames);
        orphanFilenames.removeAll(activeFilenames);

        orphanFilenames.forEach(this::deleteSafely);
    }

    private void deleteSafely(String storedFilename) {
        try {
            storageService.delete(storedFilename);
            log.info("Deleted physical file from storage: {}", storedFilename);
        } catch (RuntimeException exception) {
            log.error("Failed to delete physical file from storage: {}", storedFilename, exception);
        }
    }
}
