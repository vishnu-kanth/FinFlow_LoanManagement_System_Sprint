package com.lpu.admin_service.dto;

import lombok.Data;

@Data
public class DecisionRequest {
    private Long applicationId;
    private String decision;
    private String remarks;
}
