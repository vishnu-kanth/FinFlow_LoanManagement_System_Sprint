package com.lpu.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DecisionResponse {
    private Long applicationId;
    private String decision;
    private String message;
}