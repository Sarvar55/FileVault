package com.codems.filevault.domain.file.audit.service;

import com.codems.filevault.domain.file.entity.FileDownloadAudit;
import com.codems.filevault.domain.file.entity.FileMetadata;
import com.codems.filevault.domain.file.repository.FileDownloadAuditRepository;
import com.codems.filevault.domain.file.repository.FileMetadataRepository;
import com.codems.filevault.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FileDownloadAuditService {

    private final FileMetadataRepository fileMetadataRepository;
    private final FileDownloadAuditRepository fileDownloadAuditRepository;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(User user, Long fileId) {
        FileMetadata fileRef = fileMetadataRepository.getReferenceById(fileId);
        fileDownloadAuditRepository.save(FileDownloadAudit.of(fileRef, user));
    }
}
