package com.codems.filevault.domain.file.mapper;

import com.codems.filevault.domain.file.entity.FileMetadata;
import com.codems.filevault.domain.file.dto.FileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileMetadataMapper {

    FileResponse toResponse(FileMetadata fileMetadata);
}
