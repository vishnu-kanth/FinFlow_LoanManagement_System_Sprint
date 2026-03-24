package com.lpu.doucument_service.service;

import com.lpu.doucument_service.entity.Document;
import com.lpu.doucument_service.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private DocumentRepository repository;

    public Document saveFile(Long applicationId, MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IOException("Uploaded file must have a name");
        }

        Path filePath = uploadPath.resolve(originalFilename).normalize();

        File dest = filePath.toFile();
        file.transferTo(dest);

        Document doc = new Document();
        doc.setApplicationId(applicationId);
        doc.setFileName(originalFilename);
        doc.setFileType(file.getContentType());
        doc.setFilePath(filePath.toString());

        return repository.save(doc);
    }
}
