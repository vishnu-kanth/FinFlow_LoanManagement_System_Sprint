package com.lpu.application_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpu.application_service.dto.*;
import com.lpu.application_service.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApplicationService service;

    @InjectMocks
    private ApplicationController applicationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
    }

    @Test
    void createShouldReturnResponse() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setAmount(5000.0);
        request.setPurpose("Education");

        ApplicationResponse response = new ApplicationResponse(1L, "DRAFT");

        when(service.create(any(ApplicationRequest.class), eq(10L))).thenReturn(response);

        mockMvc.perform(post("/applications")
                        .header("X-User-Id", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void submitShouldReturnResponse() throws Exception {
        ApplicationResponse response = new ApplicationResponse(1L, "SUBMITTED");

        when(service.submit(eq(1L), eq(10L))).thenReturn(response);

        mockMvc.perform(patch("/applications/1/submit").header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void getAllShouldReturnList() throws Exception {
        ApplicationDetailResponse detail = new ApplicationDetailResponse();
        detail.setId(1L);
        detail.setStatus("APPROVED");

        when(service.getAll()).thenReturn(Collections.singletonList(detail));

        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPROVED"));
    }
}
