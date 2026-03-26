package com.lpu.document_service.controller;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping("/upload/{applicationId}")
    public ResponseEntity<Document> uploadFile(
            @PathVariable Long applicationId,
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(service.saveFile(applicationId, file));
    }
}