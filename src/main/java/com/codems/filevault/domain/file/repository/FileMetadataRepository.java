package com.codems.filevault.domain.file.repository;

import com.codems.filevault.domain.file.entity.FileMetadata;
import java.util.Optional;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    Page<FileMetadata> findAllByOwnerId(Long ownerId, Pageable pageable);

    Optional<FileMetadata> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("select file.storedFilename from FileMetadata file where file.storedFilename in :storedFilenames")
    Set<String> findExistingStoredFilenames(@Param("storedFilenames") Collection<String> storedFilenames);

    @Query(value = "select stored_filename from file_metadata where status = 'DELETED'", nativeQuery = true)
    Set<String> findDeletedStoredFilenames();
}
