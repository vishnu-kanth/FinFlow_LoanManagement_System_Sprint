package com.lpu.document_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long applicationId;


    private String fileName;
    private String fileType;
    private String filePath;


    private String status;


    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;

}