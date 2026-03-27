package com.lpu.auth_service.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String password;
}