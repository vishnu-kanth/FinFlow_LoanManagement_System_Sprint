package com.lpu.auth_service.service;

import com.lpu.auth_service.dto.AuthRequest;
import com.lpu.auth_service.dto.AuthResponse;
import com.lpu.auth_service.dto.UserUpdateRequest;
import com.lpu.auth_service.entity.User;
import com.lpu.auth_service.exception.CustomException;
import com.lpu.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesApplicantAndReturnsToken() {
        AuthRequest request = new AuthRequest();
        request.setName("Vishnu");
        request.setEmail("vishnu@example.com");
        request.setPassword("secret");

        when(repository.existsByEmail("vishnu@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(repository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getRole()).isEqualTo("ROLE_APPLICANT");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-secret");
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("vishnu@example.com");
    }

    @Test
    void loginThrowsCustomExceptionForBadCredentials() {
        AuthRequest request = new AuthRequest();
        request.setEmail("bad@example.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void updateEncodesPasswordBeforeSaving() {
        User user = new User();
        user.setId(3L);
        user.setEmail("user@example.com");
        user.setRole("ROLE_APPLICANT");
        user.setPassword("old");

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Updated");
        request.setPassword("new-password");

        when(repository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new-password");

        AuthResponse response = authService.update("user@example.com", request);

        verify(repository).save(user);
        assertThat(user.getName()).isEqualTo("Updated");
        assertThat(user.getPassword()).isEqualTo("encoded-new-password");
        assertThat(response.getEmail()).isEqualTo("user@example.com");
    }
}
