package com.lpu.document_service.controller;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('APPLICANT')")
    @PostMapping("/upload")
    public Document upload(
            @RequestParam Long applicationId,
            @RequestParam MultipartFile file,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws IOException {

        return service.saveFile(applicationId, file, idempotencyKey);
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/{id}")
    public Document getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Document> getAll() {
        return service.getAll();
    }

    @PreAuthorize("hasAnyRole('APPLICANT', 'ADMIN')")
    @GetMapping("/application/{appId}")
    public List<Document> getByApplication(@PathVariable Long appId) {
        return service.getByApplicationId(appId);
    }


    @PreAuthorize("hasRole('APPLICANT')")
    @PutMapping("/{id}")
    public Document update(@PathVariable Long id,
                           @RequestBody Document doc) {
        return service.update(id, doc);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.delete(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/verify/{id}")
    public String verify(@PathVariable Long id) {
        return service.verify(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/verified")
    public List<Document> verified() {
        return service.getVerified();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<Document> pending() {
        return service.getPending();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/type/{type}")
    public List<Document> byType(@PathVariable String type) {
        return service.getByType(type);
    }
}