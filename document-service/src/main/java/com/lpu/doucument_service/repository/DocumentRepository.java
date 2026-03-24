package com.lpu.doucument_service.repository;

import com.lpu.doucument_service.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {


}