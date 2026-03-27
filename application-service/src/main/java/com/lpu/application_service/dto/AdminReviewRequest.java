package com.lpu.application_service.dto;

import lombok.Data;

@Data
public class AdminReviewRequest {
    private String status; // APPROVED, REJECTED, NEEDS_INFO
    private String remarks;
}
