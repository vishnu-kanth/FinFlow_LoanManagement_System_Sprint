package com.lpu.application_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDetailResponse {
    private Long id;
    private Long userId;
    private Double amount;
    private String purpose;
    private Integer tenure;
    private String employmentType;
    private Double monthlyIncome;
    private String panNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewedBy;
    private String adminRemarks;
}
