package com.lpu.application_service.dto;

import lombok.Data;

@Data
public class ApplicationRequest {

    private Long userId;
    private Double amount;

}