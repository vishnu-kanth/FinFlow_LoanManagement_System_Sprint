package com.lpu.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpu.auth_service.dto.AuthRequest;
import com.lpu.auth_service.dto.AuthResponse;
import com.lpu.auth_service.dto.UserProfileResponse;
import com.lpu.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void registerShouldReturnResponse() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse();
        response.setMessage("User Registered Successfully");
        response.setEmail("test@example.com");
        response.setToken("test-token");

        when(authService.register(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void loginShouldReturnToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        AuthResponse response = new AuthResponse();
        response.setMessage("Login Successful");
        response.setEmail("test@example.com");
        response.setToken("login-token");

        when(authService.login(any(AuthRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void getProfileShouldReturnUser() throws Exception {
        UserProfileResponse profile = new UserProfileResponse(1L, "Test User", "test@example.com", "ROLE_APPLICANT");
        Principal principal = () -> "test@example.com";

        when(authService.getProfile("test@example.com")).thenReturn(profile);

        mockMvc.perform(get("/auth/profile").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }
}
