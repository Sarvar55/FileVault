package com.codems.filevault.domain.file.repository;

import com.codems.filevault.domain.file.entity.FileDownloadAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadAuditRepository extends JpaRepository<FileDownloadAudit, Long> {
}
