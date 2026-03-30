package com.lpu.admin_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.service.AdminService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void decideByIdShouldReturnResponse() throws Exception {
        DecisionRequest request = new DecisionRequest();
        request.setDecision("APPROVED");
        request.setRemarks("Eligible");

        DecisionResponse response = new DecisionResponse();
        response.setApplicationId(1L);
        response.setDecision("APPROVED");

        when(adminService.makeDecision(eq(1L), any(DecisionRequest.class), any(String.class))).thenReturn(response);

        mockMvc.perform(post("/admin/applications/1/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("APPROVED"));
    }

    @Test
    void getAllDecisionsShouldReturnList() throws Exception {
        DecisionResponse response = new DecisionResponse();
        response.setDecision("REJECTED");

        when(adminService.getAll()).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/admin/decisions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].decision").value("REJECTED"));
    }

    @Test
    void getStatsShouldReturnMap() throws Exception {
        when(adminService.getStats()).thenReturn(Map.of("totalDecisions", 5L));

        mockMvc.perform(get("/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDecisions").value(5));
    }
}
