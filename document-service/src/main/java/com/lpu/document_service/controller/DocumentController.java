package com.lpu.document_service.controller;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @PostMapping("/upload")
    public Document upload(
            @RequestParam Long applicationId,
            @RequestParam MultipartFile file) throws IOException {

        return service.saveFile(applicationId, file);
    }

    @GetMapping("/{id}")
    public Document getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @GetMapping
    public List<Document> getAll() {
        return service.getAll();
    }


    @GetMapping("/application/{appId}")
    public List<Document> getByApplication(@PathVariable Long appId) {
        return service.getByApplicationId(appId);
    }


    @PutMapping("/{id}")
    public Document update(@PathVariable Long id,
                           @RequestBody Document doc) {
        return service.update(id, doc);
    }


    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.delete(id);
    }


    @PostMapping("/verify/{id}")
    public String verify(@PathVariable Long id) {
        return service.verify(id);
    }


    @GetMapping("/verified")
    public List<Document> verified() {
        return service.getVerified();
    }

    @GetMapping("/pending")
    public List<Document> pending() {
        return service.getPending();
    }

    @GetMapping("/type/{type}")
    public List<Document> byType(@PathVariable String type) {
        return service.getByType(type);
    }
}