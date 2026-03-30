package com.lpu.document_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lpu.document_service.entity.Document;
import com.lpu.document_service.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService service;

    @InjectMocks
    private DocumentController documentController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
    }

    @Test
    void uploadShouldReturnDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "data".getBytes());
        Document doc = new Document();
        doc.setId(1L);
        doc.setApplicationId(10L);

        when(service.saveFile(eq(10L), any())).thenReturn(doc);

        mockMvc.perform(multipart("/documents/upload")
                        .file(file)
                        .param("applicationId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getByIdShouldReturnDocument() throws Exception {
        Document doc = new Document();
        doc.setId(1L);

        when(service.getById(1L)).thenReturn(doc);

        mockMvc.perform(get("/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteShouldReturnSuccess() throws Exception {
        when(service.delete(1L)).thenReturn("Document deleted successfully");

        mockMvc.perform(delete("/documents/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Document deleted successfully"));
    }
}
