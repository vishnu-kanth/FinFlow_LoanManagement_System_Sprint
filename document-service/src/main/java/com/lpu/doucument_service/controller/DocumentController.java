package com.lpu.doucument_service.controller;

import com.lpu.doucument_service.entity.Document;
import com.lpu.doucument_service.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService service;

    @PostMapping("/upload/{applicationId}")
    public ResponseEntity<Document> uploadFile(
            @PathVariable Long applicationId,
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(service.saveFile(applicationId, file));
    }
}