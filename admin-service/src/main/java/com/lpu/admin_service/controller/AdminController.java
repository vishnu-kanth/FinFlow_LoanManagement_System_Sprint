package com.lpu.admin_service.controller;

import com.lpu.admin_service.dto.DecisionRequest;
import com.lpu.admin_service.dto.DecisionResponse;
import com.lpu.admin_service.exception.CustomException;
import com.lpu.admin_service.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/applications/{id}/decision")
    public DecisionResponse decide(
            @PathVariable Long id,
            @RequestBody DecisionRequest request) {

        return service.makeDecision(id, request);
    }
}