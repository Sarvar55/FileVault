package com.codems.filevault.domain.file.entity;

import com.codems.filevault.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "file_download_audits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileDownloadAudit {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private FileMetadata file;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "downloaded_by_user_id", nullable = false)
    private User downloadedBy;

    @Column(name = "downloaded_at", nullable = false, updatable = false)
    private LocalDateTime downloadedAt;

    @PrePersist
    void prePersist() {
        this.downloadedAt = LocalDateTime.now();
    }

    public static FileDownloadAudit of(FileMetadata file, User downloadedBy) {
        FileDownloadAudit audit = new FileDownloadAudit();
        audit.setFile(file);
        audit.setDownloadedBy(downloadedBy);
        return audit;
    }
}
