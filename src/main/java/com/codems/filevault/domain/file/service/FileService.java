package com.codems.filevault.domain.file.service;

import com.codems.filevault.common.exceptions.types.BaseException;
import com.codems.filevault.domain.base.PageResponse;
import com.codems.filevault.domain.file.audit.annotation.DownloadAudited;
import com.codems.filevault.domain.file.dto.FileDownload;
import com.codems.filevault.domain.file.dto.FileResponse;
import com.codems.filevault.domain.file.dto.FileUploadRequest;
import com.codems.filevault.domain.file.dto.StoredFile;
import com.codems.filevault.domain.file.entity.FileErrorType;
import com.codems.filevault.domain.file.entity.FileMetadata;
import com.codems.filevault.domain.file.mapper.FileMetadataMapper;
import com.codems.filevault.domain.file.repository.FileMetadataRepository;
import com.codems.filevault.domain.file.validation.FileValidationService;
import com.codems.filevault.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileService {

    private final StorageService storageService;
    private final FileValidationService fileValidationService;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileMetadataMapper fileMetadataMapper;

    @Transactional
    public FileResponse upload(User owner, FileUploadRequest request, MultipartFile file) {
        fileValidationService.validate(file);
        StoredFile storedFile = storageService.store(file);
        FileMetadata metadata = FileMetadata.create(owner, request, storedFile);
        return fileMetadataMapper.toResponse(fileMetadataRepository.save(metadata));
    }

    public PageResponse<FileResponse> listOwnFiles(User owner, Pageable pageable) {
        return PageResponse.from(fileMetadataRepository.findAllByOwnerId(owner.getId(), pageable)
                .map(fileMetadataMapper::toResponse));
    }

    @Transactional
    @DownloadAudited
    public FileDownload download(User owner, Long fileId) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerId(fileId, owner.getId())
                .orElseThrow(() -> BaseException.of(FileErrorType.FILE_NOT_FOUND));

        Resource resource = storageService.load(fileMetadata.getStoredFilename());

        return new FileDownload(
                resource,
                fileMetadata.getOriginalFilename(),
                fileMetadata.getContentType(),
                fileMetadata.getSizeBytes()
        );
    }

    @Transactional
    public void delete(User owner, Long fileId) {
        FileMetadata fileMetadata = fileMetadataRepository.findByIdAndOwnerId(fileId, owner.getId())
                .orElseThrow(() -> BaseException.of(FileErrorType.FILE_NOT_FOUND));

        fileMetadata.softDelete();
    }
}
