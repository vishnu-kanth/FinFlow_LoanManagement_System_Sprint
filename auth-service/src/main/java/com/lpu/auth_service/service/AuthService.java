package com.lpu.auth_service.service;

import com.lpu.auth_service.dto.AuthRequest;
import com.lpu.auth_service.dto.AuthResponse;
import com.lpu.auth_service.entity.User;
import com.lpu.auth_service.exception.CustomException;
import com.lpu.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {

        if (request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
            throw new CustomException("Name, email and password are required");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        //  encrypted
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_APPLICANT");

        User savedUser = repository.save(user);

        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .message("User Registered Successfully")
                .token(token)
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(AuthRequest request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            throw new CustomException("Email and password are required");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new CustomException("Invalid email or password");
        }

        User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found"));

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .message("Login Successful")
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public User getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));
    }
}