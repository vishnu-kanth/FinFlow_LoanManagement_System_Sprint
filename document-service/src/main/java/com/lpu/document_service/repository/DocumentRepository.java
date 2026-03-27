package com.lpu.document_service.repository;

import com.lpu.document_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    List<Document> findByApplicationId(Long applicationId);

    List<Document> findByStatus(String status);

    List<Document> findByFileType(String fileType);
}