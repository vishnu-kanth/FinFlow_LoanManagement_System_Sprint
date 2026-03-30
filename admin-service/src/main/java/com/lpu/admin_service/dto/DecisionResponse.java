package com.lpu.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionResponse {
    private Long applicationId;
    private String decision;
    private String message;
}