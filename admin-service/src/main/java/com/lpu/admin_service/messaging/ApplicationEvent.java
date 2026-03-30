package com.lpu.admin_service.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationEvent implements Serializable {
    private Long applicationId;
    private Long userId;
    private String eventType;
    private String status;
    private LocalDateTime timestamp;
}
