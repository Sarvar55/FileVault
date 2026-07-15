package com.codems.filevault.domain.user.mapper;

import com.codems.filevault.domain.user.dto.UserResponse;
import com.codems.filevault.domain.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toResponse(User user);
}
