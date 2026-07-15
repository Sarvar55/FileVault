package com.codems.filevault.domain.auth.service;

import com.codems.filevault.common.security.service.JwtService;
import com.codems.filevault.domain.auth.dto.AuthResponse;
import com.codems.filevault.domain.auth.dto.LoginRequest;
import com.codems.filevault.domain.auth.dto.RegisterRequest;
import com.codems.filevault.domain.user.entity.User;
import com.codems.filevault.domain.user.mapper.UserMapper;
import com.codems.filevault.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegisterRequest request) {
        User user = User.of(request.username(), request.email(), passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        return token(user);
    }

    private AuthResponse token(User user) {
        return AuthResponse.of(
                jwtService.generateAccessToken(user),
                jwtService.expiresAt(),
                userMapper.toResponse(user)
        );
    }
}
