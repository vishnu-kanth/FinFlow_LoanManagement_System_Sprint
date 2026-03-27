package com.lpu.document_service.service;

import com.lpu.document_service.entity.Document;
import com.lpu.document_service.exception.CustomException;
import com.lpu.document_service.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }


    public Document saveFile(Long applicationId, MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new CustomException("File must have a valid name");
        }

        Path filePath = uploadPath.resolve(originalFilename).normalize();

        if (!filePath.startsWith(uploadPath)) {
            throw new CustomException("Invalid file path detected");
        }

        File dest = filePath.toFile();
        file.transferTo(dest);

        Document doc = new Document();
        doc.setApplicationId(applicationId);
        doc.setFileName(originalFilename);
        doc.setFileType(file.getContentType());
        doc.setFilePath(filePath.toString());
        doc.setStatus("PENDING");
        doc.setUploadedAt(LocalDateTime.now());

        return repository.save(doc);
    }


    public Document getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CustomException("Document not found"));
    }


    public List<Document> getAll() {
        return repository.findAll();
    }


    public List<Document> getByApplicationId(Long applicationId) {
        return repository.findByApplicationId(applicationId);
    }


    public Document update(Long id, Document request) {

        Document doc = getById(id);

        if (request.getFileName() != null) {
            doc.setFileName(request.getFileName());
        }

        if (request.getFileType() != null) {
            doc.setFileType(request.getFileType());
        }

        return repository.save(doc);
    }


    public String delete(Long id) {

        Document doc = getById(id);

        try {
            Files.deleteIfExists(Paths.get(doc.getFilePath()));
        } catch (IOException e) {
            throw new CustomException("Failed to delete file from storage");
        }

        repository.deleteById(id);
        return "Document deleted successfully";
    }


    public String verify(Long id) {

        Document doc = getById(id);

        doc.setStatus("VERIFIED");
        doc.setVerifiedAt(LocalDateTime.now());

        repository.save(doc);

        return "Document verified successfully";
    }


    public List<Document> getVerified() {
        return repository.findByStatus("VERIFIED");
    }


    public List<Document> getPending() {
        return repository.findByStatus("PENDING");
    }


    public List<Document> getByType(String type) {
        return repository.findByFileType(type);
    }
}