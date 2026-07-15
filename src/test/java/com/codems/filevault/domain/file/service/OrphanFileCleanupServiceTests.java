package com.codems.filevault.domain.file.service;

import static com.codems.filevault.domain.file.service.OrphanCleanupMother.ACTIVE_JPG;
import static com.codems.filevault.domain.file.service.OrphanCleanupMother.ORPHAN_PNG;
import static com.codems.filevault.domain.file.service.OrphanCleanupMother.SOFT_DELETED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codems.filevault.domain.file.repository.FileMetadataRepository;
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
        cleanupService = new OrphanFileCleanupService(
                storageService,
                fileMetadataRepository,
                OrphanCleanupMother.defaultProperties()
        );
    }

    @Test
    void should_deleteOnlyOrphanFile_when_storageContainsBothOrphanAndActiveFiles() {
        Set<String> storageFiles = Set.of(ORPHAN_PNG, ACTIVE_JPG);

        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of());
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(storageFiles);
        when(fileMetadataRepository.findExistingStoredFilenames(storageFiles)).thenReturn(Set.of(ACTIVE_JPG));

        cleanupService.cleanup();

        verify(storageService).delete(ORPHAN_PNG);
        verify(storageService, never()).delete(ACTIVE_JPG);
    }

    @Test
    void should_skipMetadataQuery_when_noFilesExistBeyondGracePeriod() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of());
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(Set.of());

        cleanupService.cleanup();

        verify(fileMetadataRepository, never()).findExistingStoredFilenames(any());
        verify(storageService, never()).delete(any());
    }

    @Test
    void should_deletePhysicalFile_when_correspondingMetadataIsSoftDeleted() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of(SOFT_DELETED));
        when(storageService.exists(SOFT_DELETED)).thenReturn(true);
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(Set.of());

        cleanupService.cleanup();

        verify(storageService).delete(SOFT_DELETED);
    }

    @Test
    void should_notDeletePhysicalFile_when_softDeletedMetadataExistsButFileIsAlreadyGone() {
        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of(SOFT_DELETED));
        when(storageService.exists(SOFT_DELETED)).thenReturn(false);
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(Set.of());

        cleanupService.cleanup();

        verify(storageService, never()).delete(SOFT_DELETED);
    }

    @Test
    void should_deleteAllOrphanFiles_when_noneHaveActiveMetadata() {
        Set<String> allOrphans = OrphanCleanupMother.orphanFilenames();

        when(fileMetadataRepository.findDeletedStoredFilenames()).thenReturn(Set.of());
        when(storageService.findStoredFilenamesModifiedBefore(any())).thenReturn(allOrphans);
        when(fileMetadataRepository.findExistingStoredFilenames(allOrphans)).thenReturn(Set.of());

        cleanupService.cleanup();

        allOrphans.forEach(filename -> verify(storageService).delete(filename));
    }
}
