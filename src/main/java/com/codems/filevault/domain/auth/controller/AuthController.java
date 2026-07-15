package com.codems.filevault.domain.auth.controller;

import com.codems.filevault.common.constants.ApplicationConstants;
import com.codems.filevault.domain.auth.dto.AuthResponse;
import com.codems.filevault.domain.auth.dto.LoginRequest;
import com.codems.filevault.domain.auth.dto.RegisterRequest;
import com.codems.filevault.domain.auth.service.AuthService;
import com.codems.filevault.domain.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/register", version = ApplicationConstants.DEFAULT_API_VERSION)
    @Operation(summary = "Register", description = "Creates a new user account without issuing an access token.")
    public ResponseEntity<BaseResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(null, HttpStatus.CREATED, "Registered successfully"));
    }

    @PostMapping(value = "/login", version = ApplicationConstants.DEFAULT_API_VERSION)
    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT access token.")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(BaseResponse.success(authService.login(request)));
    }
}
