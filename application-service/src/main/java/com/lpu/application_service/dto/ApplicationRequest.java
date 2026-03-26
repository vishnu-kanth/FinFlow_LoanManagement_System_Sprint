package com.lpu.application_service.dto;

import lombok.Data;

@Data
public class ApplicationRequest {

    private Double amount;
    private String purpose;
    private Integer tenure;
    private String employmentType;
    private Double monthlyIncome;
    private String panNumber;

}