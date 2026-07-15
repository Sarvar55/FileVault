package com.codems.filevault.domain.file.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codems.filevault.common.config.properties.FileCleanupProperties;
import com.codems.filevault.domain.file.repository.FileMetadataRepository;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrphanFileCleanupServiceTests {

    @Mock
    private StorageService storageService;

    @Mock
    private FileMetadataRepository fileMetadataRepository;

    private OrphanFileCleanupService cleanupService;

    @BeforeEach
    void setUp() {
        FileCleanupProperties properties = new FileCleanupProperties();
        properties.setOrphanGracePeriod(Duration.ofHours(24));
        cleanupService = new OrphanFileCleanupService(storageService, fileMetadataRepository, properties);
    }

    @Test
    void deletesOnlyFilesWithoutActiveMetadata() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of());
        when(storageService.findStoredFilenamesModifiedBefore(any()))
                .thenReturn(Set.of("orphan.png", "referenced.pdf"));
        when(fileMetadataRepository.findExistingStoredFilenames(any()))
                .thenReturn(Set.of("referenced.pdf"));

        cleanupService.cleanup();

        verify(storageService).delete("orphan.png");
        verify(storageService, never()).delete("referenced.pdf");
    }

    @Test
    void skipsRepositoryQueryWhenThereAreNoCandidates() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of());
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(Set.of());

        cleanupService.cleanup();

        verify(fileMetadataRepository, never()).findExistingStoredFilenames(any());
        verify(storageService, never()).delete(any());
    }

    @Test
    void deletesPhysicalFilesReferencedBySoftDeletedMetadata() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of("deleted.pdf"));
        when(storageService.exists("deleted.pdf")).thenReturn(true);
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(Set.of());

        cleanupService.cleanup();

        verify(storageService).delete("deleted.pdf");
    }
}
