package com.codems.filevault.domain.file.entity;

import com.codems.filevault.domain.base.BaseEntity;
import com.codems.filevault.domain.base.HibernateFilters;
import com.codems.filevault.domain.file.dto.FileUploadRequest;
import com.codems.filevault.domain.file.dto.StoredFile;
import com.codems.filevault.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "file_metadata")
@SQLDelete(sql = "update file_metadata set status = 'DELETED', deleted_at = current_timestamp, updated_at = current_timestamp where id = ?")
@FilterDef(
        name = HibernateFilters.ACTIVE_RECORD_FILTER,
        defaultCondition = HibernateFilters.ACTIVE_RECORD_CONDITION,
        autoEnabled = true,
        applyToLoadByKey = true
)
@Filter(name = HibernateFilters.ACTIVE_RECORD_FILTER)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class FileMetadata extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, unique = true, length = 255)
    private String storedFilename;

    @Column(name = "content_type", nullable = false, length = 120)
    private String contentType;

    @Column(nullable = false, length = 20)
    private String extension;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "sha256_checksum", nullable = false, length = 64)
    private String sha256Checksum;

    @Column(name = "storage_path", nullable = false, length = 1000)
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FileStatus status = FileStatus.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static FileMetadata create(User owner, FileUploadRequest request, StoredFile storedFile) {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setOwner(owner);
        fileMetadata.setTitle(request.title());
        fileMetadata.setDescription(request.description());
        fileMetadata.setCategory(request.category());
        fileMetadata.setOriginalFilename(storedFile.originalFilename());
        fileMetadata.setStoredFilename(storedFile.storedFilename());
        fileMetadata.setContentType(storedFile.contentType());
        fileMetadata.setExtension(storedFile.extension());
        fileMetadata.setSizeBytes(storedFile.sizeBytes());
        fileMetadata.setSha256Checksum(storedFile.sha256Checksum());
        fileMetadata.setStoragePath(storedFile.storagePath());
        fileMetadata.setStatus(FileStatus.ACTIVE);
        return fileMetadata;
    }

    public void softDelete() {
        this.status = FileStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
