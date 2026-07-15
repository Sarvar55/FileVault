package com.codems.filevault.domain.user.dto;

import com.codems.filevault.domain.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authenticated user information")
public record UserResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "sarvar")
        String username,

        @Schema(example = "sarvar@example.com")
        String email,

        @Schema(example = "USER")
        Role role
) {
}
