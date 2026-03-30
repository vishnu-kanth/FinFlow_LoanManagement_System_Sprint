package com.lpu.application_service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionEvent implements Serializable {
    private Long applicationId;
    private String decision;     // APPROVED or REJECTED
    private String remarks;
    private String adminEmail;
    private LocalDateTime timestamp;
}
